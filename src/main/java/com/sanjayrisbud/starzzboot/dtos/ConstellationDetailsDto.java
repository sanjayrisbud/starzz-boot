package com.sanjayrisbud.starzzboot.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonPropertyOrder({
    "constellationId",
    "constellationName",
    "galaxy",
    "addedBy",
    "verifiedBy"
})
public class ConstellationDetailsDto {
    private Integer constellationId;
    private String constellationName;
    private GalaxySummaryDto galaxy;
    private UserSummaryDto addedBy;
    private UserSummaryDto verifiedBy;
}
