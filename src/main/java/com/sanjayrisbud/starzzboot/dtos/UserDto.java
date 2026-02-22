package com.sanjayrisbud.starzzboot.dtos;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
}
