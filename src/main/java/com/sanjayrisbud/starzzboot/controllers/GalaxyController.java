package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.Message;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/galaxies")
public class GalaxyController {
    @GetMapping
    public Message getGalaxyList() {
        return new Message("Successfully called getGalaxyList()");
    }

    @GetMapping("/{id}")
    public Message getGalaxy(@PathVariable Long id) {
        return new Message("Successfully called getGalaxy(" + id + ")");
    }

    @PostMapping
    public Message registerGalaxy(@RequestBody Message request) {
        return new Message("Successfully called registerGalaxy(" + request + ")");
    }

    @PutMapping("/{id}")
    public Message updateGalaxy(@PathVariable Long id, @RequestBody Message request) {
        return new Message("Successfully called updateGalaxy(" + id + ", " + request + ")");
    }

    @DeleteMapping("/{id}")
    public Message deleteGalaxy(@PathVariable Long id) {
        return new Message("Successfully called deleteGalaxy(" + id + ")");
    }

}
