package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.StarDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.StarSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.mappers.StarMapper;
import com.sanjayrisbud.starzzboot.models.Star;
import com.sanjayrisbud.starzzboot.repositories.StarRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StarService {
    private final StarRepository starRepository;
    private final StarMapper starMapper;

    public List<StarSummaryDto> getStarList() {
        return starRepository.findAll().stream()
                .map(starMapper::toSummaryDto)
                .toList();
    }

    public Star findById(Integer id) {
        return starRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Star", id));
    }

    public StarDetailsDto getStar(Integer id) {
        return starMapper.toDetailsDto(findById(id));
    }
}
