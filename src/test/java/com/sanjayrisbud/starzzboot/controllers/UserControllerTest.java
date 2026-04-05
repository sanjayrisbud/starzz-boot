package com.sanjayrisbud.starzzboot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjayrisbud.starzzboot.dtos.ChangePasswordDto;
import com.sanjayrisbud.starzzboot.dtos.UserDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.UserDto;
import com.sanjayrisbud.starzzboot.dtos.UserSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.services.JwtService;
import com.sanjayrisbud.starzzboot.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.InputMismatchException;
import java.util.List;

import static com.sanjayrisbud.starzzboot.helpers.DtoFactory.*;
import static com.sanjayrisbud.starzzboot.helpers.EntityFactory.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void getUserListReturns200WithEmptyList() throws Exception {
        when(userService.getUserList()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getUserListReturns200WithListOfOneDto() throws Exception {
        User u = buildUser(1, "john");
        UserSummaryDto dto = buildUserSummaryDto(u);

        when(userService.getUserList()).thenReturn(List.of(dto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("john"));
    }

    @Test
    void getUserListReturns200WithListOfTwoDtos() throws Exception {
        User u1 = buildUser(1, "john");
        User u2 = buildUser(2, "jane");
        UserSummaryDto dto1 = buildUserSummaryDto(u1);
        UserSummaryDto dto2 = buildUserSummaryDto(u2);

        when(userService.getUserList()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].username").value("john"))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].username").value("jane"));
    }

    @Test
    void getUserGivenExistingIdReturns200AndUser() throws Exception {
        User u = buildUser();
        UserDetailsDto dto = buildUserDetailsDto(u);

        when(userService.getUser(u.getId())).thenReturn(dto);

        mockMvc.perform(get("/users/" + u.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(u.getId()));
    }

    @Test
    void getUserGivenNonExistentIdReturns404() throws Exception {
        Integer nonExistentId = 999;

        when(userService.getUser(nonExistentId))
                .thenThrow(new ResourceNotFoundException("User", nonExistentId));

        mockMvc.perform(get("/users/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerUserGivenValidDataReturns201AndAddedUser() throws Exception {
        User u = buildUser();
        UserDto request = buildUserDto();
        UserDetailsDto dto = buildUserDetailsDto(u);

        when(userService.registerUser(request)).thenReturn(dto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/users/" + u.getId())))
                .andExpect(jsonPath("$.userId").value(u.getId()));
    }

    @Test
    void registerUserGivenInvalidDataReturns400() throws Exception {
        UserDto request = buildUserDto();
        request.setUsername(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUserGivenStringDataReturns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"Invalid\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserGivenValidDataReturns200AndUpdatedUser() throws Exception {
        User u = buildUser();
        UserDto request = buildUserDto();
        UserDetailsDto dto = buildUserDetailsDto(u);

        when(userService.updateUser(u.getId(), request)).thenReturn(dto);

        mockMvc.perform(put("/users/" + u.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(u.getId()));
    }

    @Test
    void changePasswordGivenMatchingExistingPasswordReturns204() throws Exception {
        User u = buildUser();
        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        doNothing().when(userService).changePassword(u.getId(), request);

        mockMvc.perform(patch("/users/" + u.getId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePasswordGivenNonMatchingExistingPasswordReturns400() throws Exception {
        User u = buildUser();
        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        doThrow(new InputMismatchException("Passwords don't match."))
                .when(userService).changePassword(u.getId(), request);

        mockMvc.perform(patch("/users/" + u.getId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
