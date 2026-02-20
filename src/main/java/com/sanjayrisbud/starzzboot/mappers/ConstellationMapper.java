package com.sanjayrisbud.starzzboot.mappers;

import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.models.Constellation;
import org.springframework.stereotype.Component;

@Component
public class ConstellationMapper {
    public ConstellationSummaryDto toSummaryDto(Constellation constellation) {
        if (constellation == null)
            return null;

        return new ConstellationSummaryDto(constellation.getId(), constellation.getName());
    }
}
