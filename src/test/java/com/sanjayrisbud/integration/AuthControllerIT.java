package com.sanjayrisbud.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.StarzzBootApplication;
import com.sanjayrisbud.starzzboot.dtos.LoginDto;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.sanjayrisbud.starzzboot.dtos.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = StarzzBootApplication.class)
@AutoConfigureMockMvc
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.security.password-reset-sentinel}")
    private String passwordResetSentinel;

    @Test
    @Transactional
    void loginGivenValidCredentialsReturns200WithToken() throws Exception {
        User user = User.builder()
                .name("testloginuser")
                .email("testloginuser@email.com")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(user);

        LoginDto request = new LoginDto();
        request.setUsername("testloginuser");
        request.setPassword("password");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Transactional
    void loginGivenValidTokenAllowsAccessToProtectedEndpoint() throws Exception {
        User user = User.builder()
                .name("testloginuser")
                .email("testloginuser@email.com")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(user);

        LoginDto request = new LoginDto();
        request.setUsername("testloginuser");
        request.setPassword("password");

        String responseBody = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(responseBody).get("token").asText();

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void loginGivenInvalidCredentialsReturns401() throws Exception {
        User user = User.builder()
                .name("testloginuser")
                .email("testloginuser@email.com")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(user);

        LoginDto request = new LoginDto();
        request.setUsername("testloginuser");
        request.setPassword("wrongPassword");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void loginGivenUserRoleTokenAttemptingToCreateUserReturns403() throws Exception {
        User user = User.builder()
                .name("testloginuser")
                .email("testloginuser@email.com")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(user);

        LoginDto loginRequest = new LoginDto();
        loginRequest.setUsername("testloginuser");
        loginRequest.setPassword("password");

        String responseBody = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(responseBody).get("token").asText();

        UserDto createRequest = UserDto.builder()
                .username("newuser12345")
                .email("newuser@email.com")
                .build();

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void loginGivenSentinelPasswordReturns403WithUserId() throws Exception {
        User user = User.builder()
                .name("testloginuser")
                .email("testloginuser@email.com")
                .password(passwordEncoder.encode(passwordResetSentinel))
                .build();
        userRepository.save(user);

        LoginDto request = new LoginDto();
        request.setUsername("testloginuser");
        request.setPassword(passwordResetSentinel);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.userId").value(user.getId()));
    }

    @Test
    void accessToProtectedEndpointGivenInvalidTokenThrows401() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer abcde"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessToProtectedEndpointGivenNoAuthorizationThrows401() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

}
