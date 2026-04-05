package com.sanjayrisbud.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.StarzzBootApplication;
import com.sanjayrisbud.starzzboot.dtos.GalaxyDto;
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
class GalaxyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getGalaxyListReturns200AndList() throws Exception {
        mockMvc.perform(get("/galaxies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100));
    }

    @Test
    void getGalaxyGivenExistingIdReturns200AndGalaxy() throws Exception {
        mockMvc.perform(get("/galaxies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.galaxyId").value(1))
                .andExpect(jsonPath("$.galaxyName").value("GAL-LPvfm"));
    }

    @Test
    void getGalaxyGivenNonExistentIdReturns404AndError() throws Exception {
        mockMvc.perform(get("/galaxies/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerGalaxyWithoutAuthReturns401() throws Exception {
        mockMvc.perform(post("/galaxies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser
    void registerGalaxyGivenValidDataReturns201AndGalaxy() throws Exception {
        GalaxyDto request = GalaxyDto.builder()
                .galaxyName("Test Galaxy")
                .galaxyType("Spiral")
                .distanceMly(1234)
                .redshift(6)
                .massSolar(89700)
                .diameterLy(45000)
                .adderId(36)
                .build();

        mockMvc.perform(post("/galaxies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.galaxyId").isNotEmpty())
                .andExpect(jsonPath("$.galaxyName").value("Test Galaxy"));
    }

    @Test
    @Transactional
    @WithMockUser
    void updateGalaxyGivenExistingIdReturns200AndUpdatedGalaxy() throws Exception {
        GalaxyDto request = GalaxyDto.builder()
                .galaxyName("Updated Galaxy")
                .galaxyType("Spiral")
                .distanceMly(5221)
                .redshift(53)
                .massSolar(86500)
                .diameterLy(76500)
                .adderId(19)
                .build();

        mockMvc.perform(put("/galaxies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.galaxyId").value(1))
                .andExpect(jsonPath("$.galaxyName").value("Updated Galaxy"));
    }

    @Test
    @Transactional
    @WithMockUser
    void deleteGalaxyGivenExistingIdReturns204() throws Exception {
        mockMvc.perform(delete("/galaxies/1"))
                .andExpect(status().isNoContent());
    }

}
