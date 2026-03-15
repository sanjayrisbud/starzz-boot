package com.sanjayrisbud.starzzboot.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConstellationDto {
    @NotBlank private String constellationName;
    @NotNull private Integer galaxyId;
    @NotNull private Integer adderId;
    private Integer verifierId;
}
