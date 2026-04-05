package com.sanjayrisbud.starzzboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.dtos.ConstellationDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.models.Constellation;
import com.sanjayrisbud.starzzboot.services.ConstellationService;
import com.sanjayrisbud.starzzboot.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(ConstellationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConstellationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConstellationService constellationService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getConstellationListReturns200WithEmptyList() throws Exception {
        when(constellationService.getConstellationList()).thenReturn(List.of());

        mockMvc.perform(get("/constellations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getConstellationListReturns200WithListOfOneDto() throws Exception {
        Constellation c = buildConstellation(1, "Orion");
        ConstellationSummaryDto dto = buildConstellationSummaryDto(c);

        when(constellationService.getConstellationList()).thenReturn(List.of(dto));

        mockMvc.perform(get("/constellations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].constellationId").value(1))
                .andExpect(jsonPath("$[0].constellationName").value("Orion"));
    }

    @Test
    void getConstellationListReturns200WithListOfTwoDtos() throws Exception {
        Constellation c1 = buildConstellation(1, "Orion");
        Constellation c2 = buildConstellation(2, "Cassiopeia");
        ConstellationSummaryDto dto1 = buildConstellationSummaryDto(c1);
        ConstellationSummaryDto dto2 = buildConstellationSummaryDto(c2);

        when(constellationService.getConstellationList()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/constellations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].constellationId").value(1))
                .andExpect(jsonPath("$[0].constellationName").value("Orion"))
                .andExpect(jsonPath("$[1].constellationId").value(2))
                .andExpect(jsonPath("$[1].constellationName").value("Cassiopeia"));
    }

    @Test
    void getConstellationGivenExistingIdReturns200AndConstellation() throws Exception {
        Constellation c = buildConstellation();
        ConstellationDetailsDto dto = buildConstellationDetailsDto(c);

        when(constellationService.getConstellation(c.getId())).thenReturn(dto);

        mockMvc.perform(get("/constellations/" + c.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constellationId").value(c.getId()));
    }

    @Test
    void getConstellationGivenNonExistentIdReturns404AndError() throws Exception {
        Integer nonExistentId = 999;

        when(constellationService.getConstellation(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Constellation", nonExistentId));

        mockMvc.perform(get("/constellations/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerConstellationGivenValidDataReturns201AndAddedConstellation() throws Exception {
        Constellation c = buildConstellation();
        ConstellationDto request = buildConstellationDto();
        ConstellationDetailsDto dto = buildConstellationDetailsDto(c);

        when(constellationService.registerConstellation(request)).thenReturn(dto);

        mockMvc.perform(post("/constellations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/constellations/" + c.getId())))
                .andExpect(jsonPath("$.constellationName").value(c.getName()));
    }

    @Test
    void registerConstellationGivenInvalidDataReturns400() throws Exception {
        ConstellationDto request = buildConstellationDto();
        request.setConstellationName(null);

        mockMvc.perform(post("/constellations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerConstellationGivenStringDataReturns400() throws Exception {
        mockMvc.perform(post("/constellations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Invalid\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateConstellationGivenValidDataReturns200AndUpdatedConstellation() throws Exception {
        Constellation c = buildConstellation();
        ConstellationDto request = buildConstellationDto();
        c.setName(request.getConstellationName());
        ConstellationDetailsDto dto = buildConstellationDetailsDto(c);

        when(constellationService.updateConstellation(c.getId(), request)).thenReturn(dto);

        mockMvc.perform(put("/constellations/" + c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constellationName")
                        .value(request.getConstellationName()));
    }

    @Test
    void deleteConstellationGivenExistingIdReturns204() throws Exception {
        Constellation c = buildConstellation();

        doNothing().when(constellationService).deleteConstellation(c.getId());

        mockMvc.perform(delete("/constellations/" + c.getId()))
                .andExpect(status().isNoContent());
    }
}
