package com.sanjayrisbud.starzzboot.mappers;

import com.sanjayrisbud.starzzboot.dtos.StarSummaryDto;
import com.sanjayrisbud.starzzboot.models.Star;
import org.springframework.stereotype.Component;

@Component
public class StarMapper {
    public StarSummaryDto toSummaryDto(Star star) {
        if (star == null)
            return null;

        return new StarSummaryDto(star.getId(), star.getName());
    }
}
