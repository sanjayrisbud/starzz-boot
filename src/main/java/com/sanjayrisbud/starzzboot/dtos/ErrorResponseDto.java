package com.sanjayrisbud.starzzboot.dtos;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        LocalDateTime timestamp
) {}
