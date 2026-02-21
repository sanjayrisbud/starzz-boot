package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.GalaxyDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxySummaryDto;
import com.sanjayrisbud.starzzboot.mappers.GalaxyMapper;
import com.sanjayrisbud.starzzboot.models.Galaxy;
import com.sanjayrisbud.starzzboot.repositories.GalaxyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class GalaxyService {
    private final GalaxyRepository galaxyRepository;
    private final GalaxyMapper galaxyMapper;

    public List<GalaxySummaryDto> getGalaxyList() {
        return galaxyRepository.findAll().stream()
                .map(galaxyMapper::toSummaryDto)
                .toList();
    }

    public Galaxy findById(Integer id) {
        return galaxyRepository.findById(id).orElse(null);
    }

    public GalaxyDetailsDto getGalaxy(Integer id) {
        return galaxyMapper.toDetailsDto(findById(id));
    }
}
