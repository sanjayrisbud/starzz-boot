package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.mappers.ConstellationMapper;
import com.sanjayrisbud.starzzboot.repositories.ConstellationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ConstellationService {
    private final ConstellationRepository constellationRepository;
    private final ConstellationMapper constellationMapper;

    public List<ConstellationSummaryDto> findAll() {
        var constellations = constellationRepository.findAll();
        return constellations.stream()
                .map(constellationMapper::toSummaryDto)
                .toList();
    }
}
