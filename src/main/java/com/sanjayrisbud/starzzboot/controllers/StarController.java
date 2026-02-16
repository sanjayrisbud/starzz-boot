package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.Message;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stars")
public class StarController {
    @GetMapping
    public Message getStarList() {
        return new Message("Successfully called getStarList()");
    }

    @GetMapping("/{id}")
    public Message getStar(@PathVariable Long id) {
        return new Message("Successfully called getStar(" + id + ")");
    }

    @PostMapping
    public Message registerStar(@RequestBody Message request) {
        return new Message("Successfully called registerStar(" + request + ")");
    }

    @PutMapping("/{id}")
    public Message updateStar(@PathVariable Long id, @RequestBody Message request) {
        return new Message("Successfully called updateStar(" + id + ", " + request + ")");
    }

    @DeleteMapping("/{id}")
    public Message deleteStar(@PathVariable Long id) {
        return new Message("Successfully called deleteStar(" + id + ")");
    }

}
