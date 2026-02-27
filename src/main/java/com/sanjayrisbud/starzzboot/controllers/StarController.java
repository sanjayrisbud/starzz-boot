package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.Message;
import com.sanjayrisbud.starzzboot.dtos.StarDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.StarDto;
import com.sanjayrisbud.starzzboot.dtos.StarSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.services.StarService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/stars")
public class StarController {
    private final StarService starService;

    @GetMapping
    public List<StarSummaryDto> getStarList() {
        return starService.getStarList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StarDetailsDto> getStar(@PathVariable Integer id) {
        var star = starService.getStar(id);
        if (star == null)
            throw new ResourceNotFoundException("Star", id);

        return ResponseEntity.ok().body(star);
    }

    @PostMapping
    public ResponseEntity<StarDetailsDto> registerStar(
            @Valid @RequestBody StarDto request,
            UriComponentsBuilder uriBuilder) {
        var newStar = starService.registerStar(request);
        var uri = uriBuilder.path("/stars/{id}").buildAndExpand(newStar.getStarId()).toUri();
        return ResponseEntity.created(uri).body(newStar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StarDetailsDto> updateStar(
            @PathVariable Integer id,
            @Valid @RequestBody StarDto request) {
        var existingStar = starService.updateStar(id, request);
        return ResponseEntity.ok().body(existingStar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStar(@PathVariable Integer id) {
        starService.deleteStar(id);
        return ResponseEntity.noContent().build();
    }

}
