package com.sanjayrisbud.starzzboot.mappers;

import com.sanjayrisbud.starzzboot.dtos.GalaxySummaryDto;
import com.sanjayrisbud.starzzboot.models.Galaxy;
import org.springframework.stereotype.Component;

@Component
public class GalaxyMapper {
    public GalaxySummaryDto toSummaryDto(Galaxy galaxy) {
        if (galaxy == null)
            return null;

        return new GalaxySummaryDto(galaxy.getId(), galaxy.getName());
    }
}
