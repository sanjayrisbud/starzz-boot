package com.sanjayrisbud.starzzboot.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailsDto {
    private Integer userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
}
