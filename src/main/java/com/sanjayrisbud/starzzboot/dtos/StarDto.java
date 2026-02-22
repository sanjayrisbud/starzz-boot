package com.sanjayrisbud.starzzboot.dtos;

import lombok.Data;

@Data
public class StarDto {
    private String starName;
    private String starType;
    private Integer constellationId;
    private Integer rightAscension;
    private Integer declination;
    private Integer apparentMagnitude;
    private String spectralType;
    private Integer adderId;
    private Integer verifierId;
}
