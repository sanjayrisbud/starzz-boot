package com.sanjayrisbud.starzzboot.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonPropertyOrder({
    "galaxyId",
    "galaxyName",
    "galaxyType",
    "distanceMly",
    "redshift",
    "massSolar",
    "diameterLy",
    "addedBy",
    "verifiedBy"
})
public class GalaxyDetailsDto {
    private Integer galaxyId;
    private String galaxyName;
    private String galaxyType;
    private Integer distanceMly;
    private Integer redshift;
    private Integer massSolar;
    private Integer diameterLy;
    private UserSummaryDto addedBy;
    private UserSummaryDto verifiedBy;
}
