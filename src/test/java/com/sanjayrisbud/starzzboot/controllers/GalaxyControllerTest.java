package com.sanjayrisbud.starzzboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.dtos.GalaxyDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxyDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxySummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.models.Galaxy;
import com.sanjayrisbud.starzzboot.services.GalaxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.sanjayrisbud.starzzboot.helpers.DtoFactory.*;
import static com.sanjayrisbud.starzzboot.helpers.EntityFactory.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GalaxyController.class)
class GalaxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GalaxyService galaxyService;

    @Test
    void getGalaxyListReturns200WithEmptyList() throws Exception {
        when(galaxyService.getGalaxyList()).thenReturn(List.of());

        mockMvc.perform(get("/galaxies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getGalaxyListReturns200WithListOfOneDto() throws Exception {
        Galaxy g = buildGalaxy(1, "Andromeda");
        GalaxySummaryDto dto = buildGalaxySummaryDto(g);

        when(galaxyService.getGalaxyList()).thenReturn(List.of(dto));

        mockMvc.perform(get("/galaxies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].galaxyId").value(1))
                .andExpect(jsonPath("$[0].galaxyName").value("Andromeda"));
    }

    @Test
    void getGalaxyListReturns200WithListOfTwoDtos() throws Exception {
        Galaxy g1 = buildGalaxy(1, "Andromeda");
        Galaxy g2 = buildGalaxy(2, "Milky Way");
        GalaxySummaryDto dto1 = buildGalaxySummaryDto(g1);
        GalaxySummaryDto dto2 = buildGalaxySummaryDto(g2);

        when(galaxyService.getGalaxyList()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/galaxies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].galaxyId").value(1))
                .andExpect(jsonPath("$[0].galaxyName").value("Andromeda"))
                .andExpect(jsonPath("$[1].galaxyId").value(2))
                .andExpect(jsonPath("$[1].galaxyName").value("Milky Way"));
    }

    @Test
    void getGalaxyGivenExistingIdReturns200AndGalaxy() throws Exception {
        Galaxy g = buildGalaxy();
        GalaxyDetailsDto dto = buildGalaxyDetailsDto(g);

        when(galaxyService.getGalaxy(g.getId())).thenReturn(dto);

        mockMvc.perform(get("/galaxies/" + g.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.galaxyId").value(g.getId()));
    }

    @Test
    void getGalaxyGivenNonExistentIdReturns404() throws Exception {
        Integer nonExistentId = 999;

        when(galaxyService.getGalaxy(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Galaxy", nonExistentId));

        mockMvc.perform(get("/galaxies/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerGalaxyGivenValidDataReturns201AndAddedGalaxy() throws Exception {
        Galaxy g = buildGalaxy();
        GalaxyDto request = buildGalaxyDto();
        GalaxyDetailsDto dto = buildGalaxyDetailsDto(g);

        when(galaxyService.registerGalaxy(request)).thenReturn(dto);

        mockMvc.perform(post("/galaxies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/galaxies/" + g.getId())))
                .andExpect(jsonPath("$.galaxyName").value(g.getName()));
    }

    @Test
    void registerGalaxyGivenInvalidDataReturns400() throws Exception {
        GalaxyDto request = buildGalaxyDto();
        request.setGalaxyName(null);

        mockMvc.perform(post("/galaxies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerGalaxyGivenStringDataReturns400() throws Exception {
        mockMvc.perform(post("/galaxies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Invalid\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateGalaxyGivenValidDataReturns200AndUpdatedGalaxy() throws Exception {
        Galaxy g = buildGalaxy();
        GalaxyDto request = buildGalaxyDto();
        g.setName(request.getGalaxyName());
        GalaxyDetailsDto dto = buildGalaxyDetailsDto(g);

        when(galaxyService.updateGalaxy(g.getId(), request)).thenReturn(dto);

        mockMvc.perform(put("/galaxies/" + g.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.galaxyName").value(request.getGalaxyName()));
    }

    @Test
    void deleteGalaxyGivenExistingIdReturns204() throws Exception {
        Galaxy g = buildGalaxy();

        doNothing().when(galaxyService).deleteGalaxy(g.getId());

        mockMvc.perform(delete("/galaxies/" + g.getId()))
                .andExpect(status().isNoContent());
    }
}
