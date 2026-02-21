package com.sanjayrisbud.starzzboot.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonPropertyOrder({
    "starId",
    "starName",
    "starType",
    "constellation",
    "rightAscension",
    "declination",
    "apparentMagnitude",
    "spectralType",
    "addedBy",
    "verifiedBy"
})
public class StarDetailsDto {
    private Integer starId;
    private String starName;
    private String starType;
    private ConstellationSummaryDto constellation;
    private Integer rightAscension;
    private Integer declination;
    private Integer apparentMagnitude;
    private String spectralType;
    private UserSummaryDto addedBy;
    private UserSummaryDto verifiedBy;
}
