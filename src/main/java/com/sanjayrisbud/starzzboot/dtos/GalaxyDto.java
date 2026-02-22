package com.sanjayrisbud.starzzboot.dtos;

import lombok.Data;

@Data
public class GalaxyDto {
    private String galaxyName;
    private String galaxyType;
    private Integer distanceMly;
    private Integer redshift;
    private Integer massSolar;
    private Integer diameterLy;
    private Integer adderId;
    private Integer verifierId;
}
