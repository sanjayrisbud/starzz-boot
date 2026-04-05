package com.sanjayrisbud.starzzboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.dtos.LoginDto;
import com.sanjayrisbud.starzzboot.exceptions.PasswordResetRequiredException;
import com.sanjayrisbud.starzzboot.services.AuthService;
import com.sanjayrisbud.starzzboot.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void loginGivenValidCredentialsReturns200WithToken() throws Exception {
        LoginDto request = new LoginDto();
        request.setUsername("john");
        request.setPassword("password");

        when(authService.login(request)).thenReturn("jwt-token");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void loginGivenInvalidCredentialsReturns401() throws Exception {
        LoginDto request = new LoginDto();
        request.setUsername("john");
        request.setPassword("wrongPassword");

        when(authService.login(request))
                .thenThrow(new BadCredentialsException("Invalid credentials."));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginGivenSentinelPasswordReturns403WithUserId() throws Exception {
        LoginDto request = new LoginDto();
        request.setUsername("john");
        request.setPassword("password");

        when(authService.login(request))
                .thenThrow(new PasswordResetRequiredException(1));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void loginGivenInvalidDataReturns400() throws Exception {
        LoginDto request = new LoginDto();
        request.setUsername(null);
        request.setPassword("password");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginGivenStringDataReturns400() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Invalid\""))
                .andExpect(status().isBadRequest());
    }
}
