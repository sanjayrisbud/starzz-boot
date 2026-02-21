package com.sanjayrisbud.starzzboot.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonPropertyOrder({
    "constellationId",
    "constellationName"
})
public class ConstellationSummaryDto {
    private Integer constellationId;
    private String constellationName;
}
