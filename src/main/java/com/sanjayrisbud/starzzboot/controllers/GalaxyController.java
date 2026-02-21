package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.GalaxyDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxySummaryDto;
import com.sanjayrisbud.starzzboot.dtos.Message;
import com.sanjayrisbud.starzzboot.services.GalaxyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/galaxies")
public class GalaxyController {
    private final GalaxyService galaxyService;

    @GetMapping
    public List<GalaxySummaryDto> getGalaxyList() {
        return galaxyService.getGalaxyList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GalaxyDetailsDto> getGalaxy(@PathVariable Integer id) {
        var galaxy = galaxyService.getGalaxy(id);
        if (galaxy == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(galaxy);
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
