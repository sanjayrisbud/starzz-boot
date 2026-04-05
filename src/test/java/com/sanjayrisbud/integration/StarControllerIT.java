package com.sanjayrisbud.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.StarzzBootApplication;
import com.sanjayrisbud.starzzboot.dtos.StarDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = StarzzBootApplication.class)
@AutoConfigureMockMvc
class StarControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getStarListReturns200AndList() throws Exception {
        mockMvc.perform(get("/stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100));
    }

    @Test
    void getStarGivenExistingIdReturns200AndStar() throws Exception {
        mockMvc.perform(get("/stars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.starId").value(1))
                .andExpect(jsonPath("$.starName").value("S-UL520"));
    }

    @Test
    void getStarGivenNonExistentIdReturns404AndError() throws Exception {
        mockMvc.perform(get("/stars/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerStarWithoutAuthReturns401() throws Exception {
        mockMvc.perform(post("/stars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser
    void registerStarGivenValidDataReturns201AndStar() throws Exception {
        StarDto request = StarDto.builder()
                .starName("Test Star")
                .starType("red dwarf star")
                .constellationId(86)
                .rightAscension(24)
                .declination(20)
                .apparentMagnitude(-5)
                .spectralType("O")
                .adderId(62)
                .build();

        mockMvc.perform(post("/stars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.starId").isNotEmpty())
                .andExpect(jsonPath("$.starName").value("Test Star"));
    }

    @Test
    @Transactional
    @WithMockUser
    void updateStarGivenExistingIdReturns200AndUpdatedStar() throws Exception {
        StarDto request = StarDto.builder()
                .starName("Updated Star")
                .starType("red dwarf star")
                .constellationId(4)
                .rightAscension(2)
                .declination(47)
                .apparentMagnitude(-9)
                .spectralType("O")
                .adderId(88)
                .build();

        mockMvc.perform(put("/stars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.starId").value(1))
                .andExpect(jsonPath("$.starName").value("Updated Star"));
    }

    @Test
    @Transactional
    @WithMockUser
    void deleteStarGivenExistingIdReturns204() throws Exception {
        mockMvc.perform(delete("/stars/1"))
                .andExpect(status().isNoContent());
    }

}
