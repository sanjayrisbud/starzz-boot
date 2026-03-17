package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.ConstellationDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
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
    private final GalaxyService galaxyService;
    private final UserService userService;

    public List<ConstellationSummaryDto> getConstellationList() {
        return constellationRepository.findAll().stream()
                .map(constellationMapper::toSummaryDto)
                .toList();
    }

    public ConstellationDetailsDto getConstellation(Integer id) {
        return constellationMapper.toDetailsDto(findById(id));
    }

    public ConstellationDetailsDto registerConstellation(ConstellationDto request) {
        var constellation = Constellation.builder()
                .name(request.getConstellationName())
                .galaxy(galaxyService.getEntity(request.getGalaxyId(), null))
                .addedBy(userService.getEntity(request.getAdderId(), null))
                .verifiedBy(userService.getEntity(request.getVerifierId(),null))
                .build();

        constellationRepository.save(constellation);
        return constellationMapper.toDetailsDto(constellation);
    }

    public ConstellationDetailsDto updateConstellation(Integer id, ConstellationDto request) {
        Constellation currentConstellation = findById(id);

        currentConstellation.setName(request.getConstellationName());
        currentConstellation.setGalaxy(galaxyService.getEntity(
                request.getGalaxyId(), currentConstellation.getGalaxy()));
        currentConstellation.setAddedBy(userService.getEntity(
                request.getAdderId(), currentConstellation.getAddedBy()));
        currentConstellation.setVerifiedBy(userService.getEntity(
                request.getVerifierId(), currentConstellation.getVerifiedBy()));

        constellationRepository.save(currentConstellation);
        return constellationMapper.toDetailsDto(currentConstellation);
    }

    public void deleteConstellation(Integer id) {
        Constellation currentConstellation = findById(id);
        constellationRepository.delete(currentConstellation);
    }

    public Constellation getEntity(Integer newId, Constellation currentConstellation) {
        if (newId == null)
            return null;
        if (currentConstellation == null || !currentConstellation.getId().equals(newId))
            return findById(newId);
        return currentConstellation;

    }

    private Constellation findById(Integer id) {
        return constellationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Constellation", id));
    }
}
