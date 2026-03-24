package com.sanjayrisbud.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.StarzzBootApplication;
import com.sanjayrisbud.starzzboot.dtos.ConstellationDto;
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
class ConstellationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getConstellationListReturns200AndList() throws Exception {
        mockMvc.perform(get("/constellations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100));
    }

    @Test
    void getConstellationGivenExistingIdReturns200AndConstellation() throws Exception {
        mockMvc.perform(get("/constellations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constellationId").value(1))
                .andExpect(jsonPath("$.constellationName").value("CON-YKkuk"));
    }

    @Test
    void getConstellationGivenNonExistentIdReturns404AndError() throws Exception {
        mockMvc.perform(get("/constellations/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void registerConstellationGivenValidDataReturns201AndConstellation() throws Exception {
        ConstellationDto request = ConstellationDto.builder()
                .constellationName("Test Constellation")
                .galaxyId(1)
                .adderId(1)
                .build();

        mockMvc.perform(post("/constellations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.constellationId").isNotEmpty())
                .andExpect(jsonPath("$.constellationName").value("Test Constellation"));
    }

    @Test
    @Transactional
    void updateConstellationGivenExistingIdReturns200AndUpdatedConstellation() throws Exception {
        ConstellationDto request = ConstellationDto.builder()
                .constellationName("Updated Constellation")
                .galaxyId(1)
                .adderId(1)
                .build();

        mockMvc.perform(put("/constellations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constellationId").value(1))
                .andExpect(jsonPath("$.constellationName").value("Updated Constellation"));
    }

    @Test
    @Transactional
    void deleteConstellationGivenExistingIdReturns204() throws Exception {
        mockMvc.perform(delete("/constellations/1"))
                .andExpect(status().isNoContent());
    }

}
