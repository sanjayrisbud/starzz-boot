package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.UserDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.UserDto;
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

    public UserDetailsDto getUser(int id) {
        return userMapper.toDetailsDto(findById(id));
    }

    public UserDetailsDto registerUser(UserDto request) {
        var user = User.builder()
                .name(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .password("resetRequired")
                .build();
        userRepository.save(user);
        return userMapper.toDetailsDto(user);
    }

    public UserDetailsDto updateUser(Integer id, UserDto request) {
        User currentUser = findById(id);

        currentUser.setName(request.getUsername());
        currentUser.setEmail(request.getEmail());
        currentUser.setFirstName(request.getFirstName());
        currentUser.setLastName(request.getLastName());
        currentUser.setDateOfBirth(request.getDateOfBirth());

        userRepository.save(currentUser);
        return userMapper.toDetailsDto(currentUser);
    }

    public User getEntity(Integer newId, User existingUser) {
        if (newId == null)
            return null;
        if (existingUser == null || !existingUser.getId().equals(newId))
            return findById(newId);
        return existingUser;

    }

    private User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
