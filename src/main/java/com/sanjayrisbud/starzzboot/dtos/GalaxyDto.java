package com.sanjayrisbud.starzzboot.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GalaxyDto {
    @NotBlank private String galaxyName;
    @NotBlank private String galaxyType;
    @NotNull private Integer distanceMly;
    @NotNull private Integer redshift;
    @NotNull private Integer massSolar;
    @NotNull private Integer diameterLy;
    @NotNull private Integer adderId;
    private Integer verifierId;
}
