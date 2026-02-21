package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.ConstellationDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.mappers.ConstellationMapper;
import com.sanjayrisbud.starzzboot.models.Constellation;
import com.sanjayrisbud.starzzboot.repositories.ConstellationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ConstellationService {
    private final ConstellationRepository constellationRepository;
    private final ConstellationMapper constellationMapper;

    public List<ConstellationSummaryDto> getConstellationList() {
        return constellationRepository.findAll().stream()
                .map(constellationMapper::toSummaryDto)
                .toList();
    }

    public Constellation findById(Integer id) {
        return constellationRepository.findById(id).orElse(null);
    }

    public ConstellationDetailsDto getConstellation(Integer id) {
        return constellationMapper.toDetailsDto(findById(id));
    }
}
