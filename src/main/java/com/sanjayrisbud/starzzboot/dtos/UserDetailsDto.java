package com.sanjayrisbud.starzzboot.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonPropertyOrder({
        "userId",
        "username",
        "email",
        "firstName",
        "lastName",
        "dateOfBirth"
})
public class UserDetailsDto {
    private Integer userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
}
