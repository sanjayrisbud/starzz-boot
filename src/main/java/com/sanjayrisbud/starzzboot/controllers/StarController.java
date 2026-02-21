package com.sanjayrisbud.starzzboot.controllers;

import com.sanjayrisbud.starzzboot.dtos.Message;
import com.sanjayrisbud.starzzboot.dtos.StarDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.StarSummaryDto;
import com.sanjayrisbud.starzzboot.services.StarService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(star);
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
