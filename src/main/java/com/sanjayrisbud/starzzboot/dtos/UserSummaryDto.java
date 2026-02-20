package com.sanjayrisbud.starzzboot.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSummaryDto {
    private Integer userId;
    private String username;
}
