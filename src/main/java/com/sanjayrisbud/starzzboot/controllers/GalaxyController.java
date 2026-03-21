package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.GalaxyDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxyDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxySummaryDto;
import com.sanjayrisbud.starzzboot.services.GalaxyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
        return ResponseEntity.ok(galaxyService.getGalaxy(id));
    }

    @PostMapping
    public ResponseEntity<GalaxyDetailsDto> registerGalaxy(
            @Valid @RequestBody GalaxyDto request,
            UriComponentsBuilder uriBuilder) {
        var newGalaxy = galaxyService.registerGalaxy(request);
        var uri = uriBuilder.path("/galaxies/{id}")
                .buildAndExpand(newGalaxy.getGalaxyId()).toUri();
        return ResponseEntity.created(uri).body(newGalaxy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GalaxyDetailsDto> updateGalaxy(
            @PathVariable Integer id,
            @Valid @RequestBody GalaxyDto request) {
        var existingGalaxy = galaxyService.updateGalaxy(id, request);
        return ResponseEntity.ok(existingGalaxy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGalaxy(@PathVariable Integer id) {
        galaxyService.deleteGalaxy(id);
        return ResponseEntity.noContent().build();
    }

}
