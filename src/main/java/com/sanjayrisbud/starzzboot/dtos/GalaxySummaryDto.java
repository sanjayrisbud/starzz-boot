package com.sanjayrisbud.starzzboot.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({
    "galaxyId",
    "galaxyName"
})
public class GalaxySummaryDto {
    private Integer galaxyId;
    private String galaxyName;
}
