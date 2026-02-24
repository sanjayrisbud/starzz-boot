package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.ConstellationDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.mappers.ConstellationMapper;
import com.sanjayrisbud.starzzboot.models.Constellation;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.repositories.ConstellationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ConstellationService {
    private final ConstellationRepository constellationRepository;
    private final ConstellationMapper constellationMapper;
    private final GalaxyService galaxyService;
    private final UserService userService;

    public List<ConstellationSummaryDto> getConstellationList() {
        return constellationRepository.findAll().stream()
                .map(constellationMapper::toSummaryDto)
                .toList();
    }

    public Constellation findById(Integer id) {
        return constellationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Constellation", id));
    }

    public ConstellationDetailsDto getConstellation(Integer id) {
        return constellationMapper.toDetailsDto(findById(id));
    }

    public ConstellationDetailsDto registerConstellation(ConstellationDto request) {
        var galaxy = galaxyService.findById(request.getGalaxyId());
        var addedBy = userService.findById(request.getAdderId());

        User verifiedBy = null;
        var verifierId = request.getVerifierId();
        if (verifierId != null)
            verifiedBy = userService.findById(request.getVerifierId());

        var constellation = Constellation.builder()
                .name(request.getConstellationName())
                .galaxy(galaxy)
                .addedBy(addedBy)
                .verifiedBy(verifiedBy)
                .build();
        constellationRepository.save(constellation);
        return constellationMapper.toDetailsDto(constellation);
    }
}
