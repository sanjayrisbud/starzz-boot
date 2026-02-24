package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.UserDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.UserSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.mappers.UserMapper;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserSummaryDto> getUserList() {
        return userRepository.findAll().stream()
                .map(userMapper::toSummaryDto)
                .toList();
    }

    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public UserDetailsDto getUser(int id) {
        return userMapper.toDetailsDto(findById(id));
    }
}
