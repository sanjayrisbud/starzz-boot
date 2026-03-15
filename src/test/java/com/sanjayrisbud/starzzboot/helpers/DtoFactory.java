package com.sanjayrisbud.starzzboot.helpers;

import com.sanjayrisbud.starzzboot.dtos.*;
import com.sanjayrisbud.starzzboot.models.Constellation;
import com.sanjayrisbud.starzzboot.models.Galaxy;
import com.sanjayrisbud.starzzboot.models.Star;
import com.sanjayrisbud.starzzboot.models.User;

public class DtoFactory {
    public static UserSummaryDto buildUserSummaryDto(User u) {
        if (u == null)
            return null;
        return new UserSummaryDto(u.getId(), u.getName());
    }

    public static UserDetailsDto buildUserDetailsDto(User u) {
        return UserDetailsDto.builder()
                .userId(u.getId()).username(u.getName()).email(u.getEmail())
                .firstName(u.getFirstName()).lastName(u.getLastName())
                .dateOfBirth(u.getDateOfBirth())
                .build();
    }

    public static UserDto buildUserDto() {
        return UserDto.builder()
                .username("test").email("test@example.com")
                .firstName("John").lastName("Smith").dateOfBirth("2000-02-28")
                .build();
    }

    public static GalaxySummaryDto buildGalaxySummaryDto(Galaxy g) {
        return new GalaxySummaryDto(g.getId(), g.getName());
    }

    public static GalaxyDetailsDto buildGalaxyDetailsDto(Galaxy g) {
        return GalaxyDetailsDto.builder()
                .galaxyId(g.getId()).galaxyName(g.getName()).galaxyType(g.getType())
                .distanceMly(g.getDistanceMly()).redshift(g.getRedshift())
                .massSolar(g.getMassSolar()).diameterLy(g.getDiameterLy())
                .addedBy(buildUserSummaryDto(g.getAddedBy()))
                .verifiedBy(buildUserSummaryDto(g.getVerifiedBy()))
                .build();
    }

    public static GalaxyDto buildGalaxyDto() {
        return GalaxyDto.builder()
                .galaxyName("Milky Way").galaxyType("Spiral")
                .distanceMly(0).redshift(2).massSolar(465).diameterLy(5700)
                .adderId(23).verifierId(24)
                .build();
    }

    public static ConstellationSummaryDto buildConstellationSummaryDto(Constellation c) {
        return new ConstellationSummaryDto(c.getId(), c.getName());
    }

    public static ConstellationDetailsDto buildConstellationDetailsDto(Constellation c) {
        return ConstellationDetailsDto.builder()
                .constellationId(c.getId()).constellationName(c.getName())
                .galaxy(buildGalaxySummaryDto(c.getGalaxy()))
                .addedBy(buildUserSummaryDto(c.getAddedBy()))
                .verifiedBy(buildUserSummaryDto(c.getVerifiedBy()))
                .build();
    }

    public static ConstellationDto buildConstellationDto() {
        return ConstellationDto.builder()
                .constellationName("Orion").galaxyId(9)
                .adderId(23).verifierId(24)
                .build();
    }

    public static StarSummaryDto buildStarSummaryDto(Star s) {
        return new StarSummaryDto(s.getId(), s.getName());
    }

    public static StarDetailsDto buildStarDetailsDto(Star s) {
        return StarDetailsDto.builder()
                .starId(s.getId()).starName(s.getName()).starType(s.getType())
                .constellation(buildConstellationSummaryDto(s.getConstellation()))
                .rightAscension(s.getRightAscension()).declination(s.getDeclination())
                .apparentMagnitude(s.getApparentMagnitude()).spectralType(s.getSpectralType())
                .addedBy(buildUserSummaryDto(s.getAddedBy()))
                .verifiedBy(buildUserSummaryDto(s.getVerifiedBy()))
                .build();
    }

    public static StarDto buildStarDto() {
        return StarDto.builder()
                .starName("Sirius").starType("white dwarf").constellationId(15)
                .rightAscension(65).declination(28).apparentMagnitude(10).spectralType("A")
                .adderId(23).verifierId(24)
                .build();
    }
}
