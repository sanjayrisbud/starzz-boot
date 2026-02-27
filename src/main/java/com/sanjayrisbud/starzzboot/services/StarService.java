package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.StarDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.StarDto;
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
    private final ConstellationService constellationService;
    private final UserService userService;

    public List<StarSummaryDto> getStarList() {
        return starRepository.findAll().stream()
                .map(starMapper::toSummaryDto)
                .toList();
    }

    public StarDetailsDto getStar(Integer id) {
        return starMapper.toDetailsDto(findById(id));
    }

    public StarDetailsDto registerStar(StarDto request) {
        var star = Star.builder()
                .name(request.getStarName())
                .type(request.getStarType())
                .rightAscension(request.getRightAscension())
                .declination(request.getDeclination())
                .apparentMagnitude(request.getApparentMagnitude())
                .spectralType(request.getSpectralType())
                .constellation(constellationService.getEntity(request.getConstellationId(), null))
                .addedBy(userService.getEntity(request.getAdderId(), null))
                .verifiedBy(userService.getEntity(request.getVerifierId(), null))
                .build();

        starRepository.save(star);
        return starMapper.toDetailsDto(star);
    }

    public StarDetailsDto updateStar(Integer id, StarDto request) {
        var currentStar = findById(id);

        if (!currentStar.getName().equals(request.getStarName()))
            currentStar.setName(request.getStarName());
        if (!currentStar.getType().equals(request.getStarType()))
            currentStar.setType(request.getStarType());
        if (!currentStar.getRightAscension().equals(request.getRightAscension()))
            currentStar.setRightAscension(request.getRightAscension());
        if (!currentStar.getDeclination().equals(request.getDeclination()))
            currentStar.setDeclination(request.getDeclination());
        if (!currentStar.getApparentMagnitude().equals(request.getApparentMagnitude()))
            currentStar.setApparentMagnitude(request.getApparentMagnitude());
        if (!currentStar.getSpectralType().equals(request.getSpectralType()))
            currentStar.setSpectralType(request.getSpectralType());
        currentStar.setConstellation(constellationService.getEntity(
                request.getConstellationId(), currentStar.getConstellation()));
        currentStar.setAddedBy(userService.getEntity(
                request.getAdderId(), currentStar.getAddedBy()));
        currentStar.setVerifiedBy(userService.getEntity(
                request.getVerifierId(), currentStar.getVerifiedBy()));

        starRepository.save(currentStar);
        return starMapper.toDetailsDto(currentStar);
    }

    public void deleteStar(Integer id) {
        var currentStar = findById(id);
        starRepository.delete(currentStar);
    }

    private Star findById(Integer id) {
        return starRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Star", id));
    }
}
