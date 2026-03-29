package com.sanjayrisbud.starzzboot.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangePasswordDto {
    @NotBlank private String existingPassword;
    @NotBlank private String newPassword;
}
