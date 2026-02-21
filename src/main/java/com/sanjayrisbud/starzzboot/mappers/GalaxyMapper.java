package com.sanjayrisbud.starzzboot.mappers;

import com.sanjayrisbud.starzzboot.dtos.GalaxyDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxySummaryDto;
import com.sanjayrisbud.starzzboot.models.Galaxy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class GalaxyMapper {
    private final UserMapper userMapper;

    public GalaxySummaryDto toSummaryDto(Galaxy galaxy) {
        if (galaxy == null)
            return null;

        return new GalaxySummaryDto(galaxy.getId(), galaxy.getName());
    }

    public GalaxyDetailsDto toDetailsDto(Galaxy galaxy) {
        if (galaxy == null)
            return null;

        var addedBy = userMapper.toSummaryDto(galaxy.getAddedBy());
        var verifiedBy = userMapper.toSummaryDto(galaxy.getVerifiedBy());
        return GalaxyDetailsDto.builder()
                .galaxyId(galaxy.getId())
                .galaxyName(galaxy.getName())
                .galaxyType(galaxy.getType())
                .distanceMly(galaxy.getDistanceMly())
                .redshift(galaxy.getRedshift())
                .massSolar(galaxy.getMassSolar())
                .diameterLy(galaxy.getDiameterLy())
                .addedBy(addedBy)
                .verifiedBy(verifiedBy)
                .build();
    }
}
