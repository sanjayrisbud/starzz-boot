package com.sanjayrisbud.starzzboot.dtos;

import java.time.LocalDateTime;

public record PasswordResetRequiredDto(
        String message,
        LocalDateTime timestamp,
        Integer userId
) {}
