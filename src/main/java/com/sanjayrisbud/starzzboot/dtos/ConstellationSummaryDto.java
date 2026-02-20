package com.sanjayrisbud.starzzboot.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConstellationSummaryDto {
    private Integer constellationId;
    private String constellationName;
}
