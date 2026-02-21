package com.sanjayrisbud.starzzboot.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonPropertyOrder({
    "userId",
    "username"
})
public class UserSummaryDto {
    private Integer userId;
    private String username;
}
