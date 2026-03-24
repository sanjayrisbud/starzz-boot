package com.sanjayrisbud.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.StarzzBootApplication;
import com.sanjayrisbud.starzzboot.dtos.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = StarzzBootApplication.class)
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserListReturns200AndList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100));
    }

    @Test
    void getUserGivenExistingIdReturns200AndUser() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("ebalducci29128"));
    }

    @Test
    void getUserGivenNonExistentIdReturns404AndError() throws Exception {
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void registerUserGivenValidDataReturns201AndUser() throws Exception {
        UserDto request = UserDto.builder()
                .username("testuser12345")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testuser12345"));
    }

    @Test
    @Transactional
    void updateUserGivenExistingIdReturns200AndUpdatedUser() throws Exception {
        UserDto request = UserDto.builder()
                .username("updateduser12345")
                .build();

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("updateduser12345"));
    }

}
