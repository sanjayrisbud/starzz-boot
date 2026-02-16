package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.Message;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @GetMapping
    public Message getUserList() {
        return new Message("Successfully called getUserList()");
    }

    @GetMapping("/{id}")
    public Message getUser(@PathVariable Long id) {
        return new Message("Successfully called getUser(" + id + ")");
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
