package com.sanjayrisbud.starzzboot.mappers;

import com.sanjayrisbud.starzzboot.dtos.UserDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.UserSummaryDto;
import com.sanjayrisbud.starzzboot.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserSummaryDto toSummaryDto(User user) {
        if (user == null)
            return null;

        return new UserSummaryDto(user.getId(), user.getName());
    }

    public UserDetailsDto toDetailsDto(User user) {
        if (user == null)
            return null;

        return new UserDetailsDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getDateOfBirth());
    }
}
