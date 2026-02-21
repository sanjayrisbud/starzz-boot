package com.sanjayrisbud.starzzboot.mappers;

import com.sanjayrisbud.starzzboot.dtos.StarDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.StarSummaryDto;
import com.sanjayrisbud.starzzboot.models.Star;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class StarMapper {
    private final ConstellationMapper constellationMapper;
    private final UserMapper userMapper;

    public StarSummaryDto toSummaryDto(Star star) {
        if (star == null)
            return null;

        return new StarSummaryDto(star.getId(), star.getName());
    }

    public StarDetailsDto toDetailsDto(Star star) {
        if (star == null)
            return null;

        var constellation = constellationMapper.toSummaryDto(star.getConstellation());
        var addedBy = userMapper.toSummaryDto(star.getAddedBy());
        var verifiedBy = userMapper.toSummaryDto(star.getVerifiedBy());
        return StarDetailsDto.builder()
                .starId(star.getId())
                .starName(star.getName())
                .starType(star.getType())
                .constellation(constellation)
                .rightAscension(star.getRightAscension())
                .declination(star.getDeclination())
                .apparentMagnitude(star.getApparentMagnitude())
                .spectralType(star.getSpectralType())
                .addedBy(addedBy)
                .verifiedBy(verifiedBy)
                .build();
    }
}
