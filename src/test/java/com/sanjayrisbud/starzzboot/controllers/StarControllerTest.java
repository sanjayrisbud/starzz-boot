package com.sanjayrisbud.starzzboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.dtos.StarDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.StarDto;
import com.sanjayrisbud.starzzboot.dtos.StarSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.models.Star;
import com.sanjayrisbud.starzzboot.services.JwtService;
import com.sanjayrisbud.starzzboot.services.StarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

@WebMvcTest(StarController.class)
@AutoConfigureMockMvc(addFilters = false)
class StarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StarService starService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getStarListReturns200WithEmptyList() throws Exception {
        when(starService.getStarList()).thenReturn(List.of());

        mockMvc.perform(get("/stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getStarListReturns200WithListOfOneDto() throws Exception {
        Star s = buildStar(1, "Sirius");
        StarSummaryDto dto = buildStarSummaryDto(s);

        when(starService.getStarList()).thenReturn(List.of(dto));

        mockMvc.perform(get("/stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].starId").value(1))
                .andExpect(jsonPath("$[0].starName").value("Sirius"));
    }

    @Test
    void getStarListReturns200WithListOfTwoDtos() throws Exception {
        Star s1 = buildStar(1, "Sirius");
        Star s2 = buildStar(2, "Rigel");
        StarSummaryDto dto1 = buildStarSummaryDto(s1);
        StarSummaryDto dto2 = buildStarSummaryDto(s2);

        when(starService.getStarList()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].starId").value(1))
                .andExpect(jsonPath("$[0].starName").value("Sirius"))
                .andExpect(jsonPath("$[1].starId").value(2))
                .andExpect(jsonPath("$[1].starName").value("Rigel"));
    }

    @Test
    void getStarGivenExistingIdReturns200AndStar() throws Exception {
        Star s = buildStar();
        StarDetailsDto dto = buildStarDetailsDto(s);

        when(starService.getStar(s.getId())).thenReturn(dto);

        mockMvc.perform(get("/stars/" + s.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.starId").value(s.getId()));
    }

    @Test
    void getStarGivenNonExistentIdReturns404() throws Exception {
        Integer nonExistentId = 999;

        when(starService.getStar(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Star", nonExistentId));

        mockMvc.perform(get("/stars/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerStarGivenValidDataReturns201AndAddedStar() throws Exception {
        Star s = buildStar();
        StarDto request = buildStarDto();
        StarDetailsDto dto = buildStarDetailsDto(s);

        when(starService.registerStar(request)).thenReturn(dto);

        mockMvc.perform(post("/stars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/stars/" + s.getId())))
                .andExpect(jsonPath("$.starName").value(s.getName()));
    }

    @Test
    void registerStarGivenInvalidDataReturns400() throws Exception {
        StarDto request = buildStarDto();
        request.setStarName(null);

        mockMvc.perform(post("/stars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerStarGivenStringDataReturns400() throws Exception {
        mockMvc.perform(post("/stars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Invalid\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStarGivenValidDataReturns200AndUpdatedStar() throws Exception {
        Star s = buildStar();
        StarDto request = buildStarDto();
        s.setName(request.getStarName());
        StarDetailsDto dto = buildStarDetailsDto(s);

        when(starService.updateStar(s.getId(), request)).thenReturn(dto);

        mockMvc.perform(put("/stars/" + s.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.starName").value(request.getStarName()));
    }

    @Test
    void deleteStarGivenExistingIdReturns204() throws Exception {
        Star s = buildStar();

        doNothing().when(starService).deleteStar(s.getId());

        mockMvc.perform(delete("/stars/" + s.getId()))
                .andExpect(status().isNoContent());
    }
}
