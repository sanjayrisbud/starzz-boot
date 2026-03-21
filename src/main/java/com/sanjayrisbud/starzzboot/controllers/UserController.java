package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.*;
import com.sanjayrisbud.starzzboot.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserSummaryDto> getUserList() {
        return userService.getUserList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping
    public ResponseEntity<UserDetailsDto> registerUser(
            @Valid @RequestBody UserDto request,
            UriComponentsBuilder uriBuilder) {
        var newUser = userService.registerUser(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(newUser.getUserId()).toUri();
        return ResponseEntity.created(uri).body(newUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDetailsDto> updateUser(
            @PathVariable Integer id, @Valid @RequestBody UserDto request) {
        var existingUser = userService.updateUser(id, request);
        return ResponseEntity.ok(existingUser);
    }

}
