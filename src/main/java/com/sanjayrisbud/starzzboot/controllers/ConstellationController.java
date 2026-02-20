package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.dtos.Message;
import com.sanjayrisbud.starzzboot.services.ConstellationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/constellations")
public class ConstellationController {

    private ConstellationService constellationService;

    @GetMapping
    public List<ConstellationSummaryDto> getConstellationList() {
        return constellationService.findAll();
    }

    @GetMapping("/{id}")
    public Message getConstellation(@PathVariable Long id) {
        return new Message("Successfully called getConstellation(" + id + ")");
    }

    @PostMapping
    public Message registerConstellation(@RequestBody Message request) {
        return new Message("Successfully called registerConstellation(" + request + ")");
    }

    @PutMapping("/{id}")
    public Message updateConstellation(@PathVariable Long id, @RequestBody Message request) {
        return new Message("Successfully called updateConstellation(" + id + ", " + request + ")");
    }

    @DeleteMapping("/{id}")
    public Message deleteConstellation(@PathVariable Long id) {
        return new Message("Successfully called deleteConstellation(" + id + ")");
    }

}
