package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.services.ConstellationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(ConstellationController.class)
class ConstellationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConstellationService constellationService;
}