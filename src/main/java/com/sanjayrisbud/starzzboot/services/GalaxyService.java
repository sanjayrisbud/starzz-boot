package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.GalaxyDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxyDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxySummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
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
    private final UserService userService;

    public List<GalaxySummaryDto> getGalaxyList() {
        return galaxyRepository.findAll().stream()
                .map(galaxyMapper::toSummaryDto)
                .toList();
    }

    public GalaxyDetailsDto getGalaxy(Integer id) {
        return galaxyMapper.toDetailsDto(findById(id));
    }

    public GalaxyDetailsDto registerGalaxy(GalaxyDto request) {
        var galaxy = Galaxy.builder()
                .name(request.getGalaxyName())
                .type(request.getGalaxyType())
                .distanceMly(request.getDistanceMly())
                .redshift(request.getRedshift())
                .massSolar(request.getMassSolar())
                .diameterLy(request.getDiameterLy())
                .addedBy(userService.getEntity(request.getAdderId(), null))
                .verifiedBy(userService.getEntity(request.getVerifierId(), null))
                .build();

        galaxyRepository.save(galaxy);
        return galaxyMapper.toDetailsDto(galaxy);
    }

    public GalaxyDetailsDto updateGalaxy(Integer id, GalaxyDto request) {
        Galaxy currentGalaxy = findById(id);

        currentGalaxy.setName(request.getGalaxyName());
        currentGalaxy.setType(request.getGalaxyType());
        currentGalaxy.setDistanceMly(request.getDistanceMly());
        currentGalaxy.setRedshift(request.getRedshift());
        currentGalaxy.setMassSolar(request.getMassSolar());
        currentGalaxy.setDiameterLy(request.getDiameterLy());
        currentGalaxy.setAddedBy(userService.getEntity(
                request.getAdderId(), currentGalaxy.getAddedBy()));
        currentGalaxy.setVerifiedBy(userService.getEntity(
                request.getVerifierId(), currentGalaxy.getVerifiedBy()));

        galaxyRepository.save(currentGalaxy);
        return galaxyMapper.toDetailsDto(currentGalaxy);
    }

    public void deleteGalaxy(Integer id) {
        Galaxy currentGalaxy = findById(id);
        galaxyRepository.delete(currentGalaxy);
    }

    public Galaxy getEntity(Integer newId, Galaxy existingGalaxy) {
        if (newId == null)
            return null;
        if (existingGalaxy == null || !existingGalaxy.getId().equals(newId))
            return findById(newId);
        return existingGalaxy;
    }

    private Galaxy findById(Integer id) {
        return galaxyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Galaxy", id));
    }
}
