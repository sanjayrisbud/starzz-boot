package com.sanjayrisbud.starzzboot.mappers;

import com.sanjayrisbud.starzzboot.dtos.ConstellationDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.models.Constellation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ConstellationMapper {
    private final GalaxyMapper galaxyMapper;
    private final UserMapper userMapper;

    public ConstellationSummaryDto toSummaryDto(Constellation constellation) {
        if (constellation == null)
            return null;

        return new ConstellationSummaryDto(constellation.getId(), constellation.getName());
    }

    public ConstellationDetailsDto toDetailsDto(Constellation constellation) {
        if (constellation == null)
            return null;

        var galaxy = galaxyMapper.toSummaryDto(constellation.getGalaxy());
        var addedBy = userMapper.toSummaryDto(constellation.getAddedBy());
        var verifiedBy = userMapper.toSummaryDto(constellation.getVerifiedBy());

        return ConstellationDetailsDto.builder()
                .constellationId(constellation.getId())
                .constellationName(constellation.getName())
                .galaxy(galaxy)
                .addedBy(addedBy)
                .verifiedBy(verifiedBy)
                .build();
    }
}
