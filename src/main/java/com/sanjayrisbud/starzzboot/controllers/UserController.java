package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.Message;
import com.sanjayrisbud.starzzboot.dtos.UserDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.UserSummaryDto;
import com.sanjayrisbud.starzzboot.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        var user = userService.getUser(id);
        if (user == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(user);
    }

    @PostMapping
    public Message registerUser(@RequestBody Message request) {
        return new Message("Successfully called registerUser(" + request + ")");
    }

    @PutMapping("/{id}")
    public Message updateUser(@PathVariable Long id, @RequestBody Message request) {
        return new Message("Successfully called updateUser(" + id + ", " + request + ")");
    }

    @DeleteMapping("/{id}")
    public Message deleteUser(@PathVariable Long id) {
        return new Message("Successfully called deleteUser(" + id + ")");
    }

}
