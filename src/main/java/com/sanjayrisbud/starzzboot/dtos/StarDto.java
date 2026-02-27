package com.sanjayrisbud.starzzboot.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StarDto {
    @NotBlank private String starName;
    @NotBlank private String starType;
    @NotNull private Integer constellationId;
    @NotNull private Integer rightAscension;
    @NotNull private Integer declination;
    @NotNull private Integer apparentMagnitude;
    @NotNull private String spectralType;
    @NotNull private Integer adderId;
    private Integer verifierId;
}
