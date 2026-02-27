package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.ConstellationDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.services.ConstellationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/constellations")
public class ConstellationController {

    private final ConstellationService constellationService;

    @GetMapping
    public List<ConstellationSummaryDto> getConstellationList() {
        return constellationService.getConstellationList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConstellationDetailsDto> getConstellation(@PathVariable Integer id) {
        return ResponseEntity.ok(constellationService.getConstellation(id));
    }

    @PostMapping
    public ResponseEntity<ConstellationDetailsDto> registerConstellation(
            @Valid @RequestBody ConstellationDto request,
            UriComponentsBuilder uriBuilder) {
        var newConstellation = constellationService.registerConstellation(request);
        var uri = uriBuilder.path("/constellations/{id}")
                .buildAndExpand(newConstellation.getConstellationId()).toUri();
        return ResponseEntity.created(uri).body(newConstellation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConstellationDetailsDto> updateConstellation(
            @PathVariable Integer id,
            @Valid @RequestBody ConstellationDto request) {
        var existingConstellation = constellationService.updateConstellation(id, request);
        return ResponseEntity.ok(existingConstellation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConstellation(@PathVariable Integer id) {
        constellationService.deleteConstellation(id);
        return ResponseEntity.noContent().build();
    }

}
