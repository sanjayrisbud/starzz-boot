package com.sanjayrisbud.starzzboot.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonPropertyOrder({
    "starId",
    "starName"
})
public class StarSummaryDto {
    private Integer starId;
    private String starName;
}
