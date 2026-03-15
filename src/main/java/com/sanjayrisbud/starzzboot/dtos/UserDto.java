package com.sanjayrisbud.starzzboot.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto {
    @NotBlank private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
}
