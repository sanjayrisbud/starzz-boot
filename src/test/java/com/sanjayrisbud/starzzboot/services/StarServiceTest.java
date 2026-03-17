package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.StarDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.StarDto;
import com.sanjayrisbud.starzzboot.dtos.StarSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.mappers.ConstellationMapper;
import com.sanjayrisbud.starzzboot.mappers.GalaxyMapper;
import com.sanjayrisbud.starzzboot.mappers.StarMapper;
import com.sanjayrisbud.starzzboot.mappers.UserMapper;
import com.sanjayrisbud.starzzboot.models.Constellation;
import com.sanjayrisbud.starzzboot.models.Star;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.repositories.StarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sanjayrisbud.starzzboot.helpers.DtoFactory.*;
import static com.sanjayrisbud.starzzboot.helpers.EntityFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StarServiceTest {
    private final UserMapper userMapper = new UserMapper();
    private final GalaxyMapper galaxyMapper = new GalaxyMapper(userMapper);
    private final ConstellationMapper constellationMapper = new ConstellationMapper(galaxyMapper, userMapper);
    @Spy
    private final StarMapper starMapper = new StarMapper(constellationMapper, userMapper);

    @Mock
    private StarRepository starRepository;
    @Mock
    private ConstellationService constellationService;
    @Mock
    private UserService userService;
    @InjectMocks
    private StarService starService;

    @Test
    void getStarListGivenNoStarsFoundReturnsEmptyList() {
        List<Star> starList = new ArrayList<>();

        when(starRepository.findAll()).thenReturn(starList);

        List<StarSummaryDto> listing = starService.getStarList();

        assertEquals(0, listing.size());
    }

    @Test
    void getStarListGivenOneStarFoundReturnsListWithOneDto() {
        List<Star> starList = new ArrayList<>();
        List<StarSummaryDto> expected = new ArrayList<>();
        Star c = buildStar();
        starList.add(c);
        expected.add(buildStarSummaryDto(c));

        when(starRepository.findAll()).thenReturn(starList);

        List<StarSummaryDto> listing = starService.getStarList();

        assertEquals(1, listing.size());
        assertEquals(expected, listing);
    }

    @Test
    void getStarListGivenTwoStarsFoundReturnsListWithTwoDtos() {
        List<Star> starList = new ArrayList<>();
        List<StarSummaryDto> expected = new ArrayList<>();
        Star c1 = buildStar(1, "Test 1");
        Star c2 = buildStar(2, "Test 2");
        starList.add(c1);
        starList.add(c2);
        for (var c : starList)
            expected.add(buildStarSummaryDto(c));

        when(starRepository.findAll()).thenReturn(starList);

        List<StarSummaryDto> listing = starService.getStarList();

        assertEquals(expected.size(), listing.size());
        assertEquals(expected, listing);
    }

    @Test
    void getStarGivenStarIdReturnsStarDetailsDto() {
        Constellation constellation = buildConstellation(5, "constellationName");
        User addedBy = buildUser(10, "addedBy");
        User verifiedBy = buildUser(11, "verifiedBy");
        Star s = buildStar(1, "starName", constellation, addedBy, verifiedBy);
        StarDetailsDto expected = buildStarDetailsDto(s);

        when(starRepository.findById(1)).thenReturn(Optional.of(s));

        StarDetailsDto dto = starService.getStar(1);

        assertEquals(expected, dto);
    }

    @Test
    void getStarGivenIdNotFoundThrowsResourceNotFoundException() {
        Integer nonExistentId = 999;
        String exceptionMessage = "Star with id 999 not found.";

        when(starRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex1 = assertThrows(ResourceNotFoundException.class,
                () -> starService.getStar(nonExistentId));

        assertEquals(exceptionMessage, ex1.getMessage());
    }

    @Test
    void registerStarGivenCompleteDataAddsNewStar() {
        StarDto request = buildStarDto();

        Constellation constellation = buildConstellation(
                request.getConstellationId(), "constellationName");
        User addedBy = buildUser(request.getAdderId(), "addedBy");
        User verifiedBy = buildUser(request.getVerifierId(), "verifiedBy");
        Star s = Star.builder()
                .id(10).name(request.getStarName()).type(request.getStarType())
                .constellation(constellation)
                .rightAscension(request.getRightAscension())
                .declination(request.getDeclination())
                .apparentMagnitude(request.getApparentMagnitude())
                .spectralType(request.getSpectralType())
                .addedBy(addedBy).verifiedBy(verifiedBy)
                .build();

        StarDetailsDto expected = buildStarDetailsDto(s);
        // since the id is supposed to be generated by the database, and we are mocking it,
        // we expect the id in the result to be null
        expected.setStarId(null);

        when(constellationService.getEntity(
                request.getConstellationId(), null))
                .thenReturn(constellation);
        when(userService.getEntity(request.getAdderId(), null))
                .thenReturn(addedBy);
        when(userService.getEntity(request.getVerifierId(), null))
                .thenReturn(verifiedBy);

        StarDetailsDto dto = starService.registerStar(request);

        assertEquals(expected, dto);
        verify(starRepository).save(any(Star.class));
    }

    @Test
    void registerStarGivenNullVerifierAddsNewStar() {
        StarDto request = buildStarDto();
        request.setVerifierId(null);

        Constellation constellation = buildConstellation(
                request.getConstellationId(), "constellationName");
        User addedBy = buildUser(request.getAdderId(), "addedBy");
        Star s = Star.builder()
                .id(10).name(request.getStarName()).type(request.getStarType())
                .constellation(constellation)
                .rightAscension(request.getRightAscension())
                .declination(request.getDeclination())
                .apparentMagnitude(request.getApparentMagnitude())
                .spectralType(request.getSpectralType())
                .addedBy(addedBy).verifiedBy(null)
                .build();

        StarDetailsDto expected = buildStarDetailsDto(s);
        // since the id is supposed to be generated by the database, and we are mocking it,
        // we expect the id in the result to be null
        expected.setStarId(null);

        when(constellationService.getEntity(
                request.getConstellationId(), null))
                .thenReturn(constellation);
        when(userService.getEntity(request.getAdderId(), null))
                .thenReturn(addedBy);
        when(userService.getEntity(request.getVerifierId(), null))
                .thenReturn(null);

        StarDetailsDto dto = starService.registerStar(request);

        assertEquals(expected, dto);
        verify(starRepository).save(any(Star.class));
    }

    @Test
    void updateStarGivenAllDataChangedUpdatesStarFully() {
        Integer id = 10;
        StarDto request = buildStarDto();

        Star existing = buildStar();
        existing.setId(id);
        Constellation constellation = buildConstellation(
                request.getConstellationId(), "constellationName");
        User addedBy = buildUser(request.getAdderId(), "addedBy");
        User verifiedBy = buildUser(request.getVerifierId(), "verifiedBy");
        Star updated = Star.builder()
                .id(id).name(request.getStarName()).type(request.getStarType())
                .constellation(constellation)
                .rightAscension(request.getRightAscension())
                .declination(request.getDeclination())
                .apparentMagnitude(request.getApparentMagnitude())
                .spectralType(request.getSpectralType())
                .addedBy(addedBy).verifiedBy(verifiedBy)
                .build();

        StarDetailsDto expected = buildStarDetailsDto(updated);

        when(starRepository.findById(id))
                .thenReturn(Optional.of(existing));
        when(constellationService.getEntity(
                request.getConstellationId(), existing.getConstellation()))
                .thenReturn(constellation);
        when(userService.getEntity(request.getAdderId(), existing.getAddedBy()))
                .thenReturn(addedBy);
        when(userService.getEntity(request.getVerifierId(), existing.getVerifiedBy()))
                .thenReturn(verifiedBy);

        StarDetailsDto dto = starService.updateStar(id, request);

        assertEquals(expected, dto);
        verify(starRepository).save(any(Star.class));
    }

    @Test
    void updateStarGivenSomeDataChangedUpdatesStarPartially() {
        Integer id = 10;
        StarDto request = buildStarDto();
        request.setStarName("testStar");
        request.setApparentMagnitude(3);
        request.setVerifierId(34);

        Star existing = buildStar();
        existing.setId(id);
        existing.setVerifiedBy(null);

        // set the request's fields to those "saved" in the database
        request.setStarType(existing.getType());
        request.setRightAscension(existing.getRightAscension());
        request.setDeclination(existing.getDeclination());
        request.setSpectralType(existing.getSpectralType());
        request.setConstellationId(existing.getConstellation().getId());
        request.setAdderId(existing.getAddedBy().getId());

        User verifiedBy = buildUser(request.getVerifierId(), "verifiedBy");
        Star updated = Star.builder()
                .id(id).name(request.getStarName()).type(existing.getType())
                .constellation(existing.getConstellation())
                .rightAscension(existing.getRightAscension())
                .declination(existing.getDeclination())
                .apparentMagnitude(request.getApparentMagnitude())
                .spectralType(existing.getSpectralType())
                .addedBy(existing.getAddedBy()).verifiedBy(verifiedBy)
                .build();

        StarDetailsDto expected = buildStarDetailsDto(updated);

        when(starRepository.findById(id))
                .thenReturn(Optional.of(existing));
        when(constellationService.getEntity(
                request.getConstellationId(), existing.getConstellation()))
                .thenReturn(existing.getConstellation());
        when(userService.getEntity(request.getAdderId(), existing.getAddedBy()))
                .thenReturn(existing.getAddedBy());
        when(userService.getEntity(request.getVerifierId(), existing.getVerifiedBy()))
                .thenReturn(verifiedBy);

        StarDetailsDto dto = starService.updateStar(id, request);

        assertEquals(expected, dto);
        verify(starRepository).save(any(Star.class));
    }

    @Test
    void updateStarGivenNullVerifierUpdatesVerifiedByToNull() {
        Integer id = 10;
        StarDto request = buildStarDto();
        request.setVerifierId(null);

        Star existing = buildStar();
        existing.setId(id);

        // set the request's fields to those "saved" in the database
        request.setStarName(existing.getName());
        request.setStarType(existing.getType());
        request.setRightAscension(existing.getRightAscension());
        request.setDeclination(existing.getDeclination());
        request.setApparentMagnitude(existing.getApparentMagnitude());
        request.setSpectralType(existing.getSpectralType());
        request.setConstellationId(existing.getConstellation().getId());
        request.setAdderId(existing.getAddedBy().getId());

        Star updated = Star.builder()
                .id(id).name(existing.getName()).type(existing.getType())
                .constellation(existing.getConstellation())
                .rightAscension(existing.getRightAscension())
                .declination(existing.getDeclination())
                .apparentMagnitude(existing.getApparentMagnitude())
                .spectralType(existing.getSpectralType())
                .addedBy(existing.getAddedBy()).verifiedBy(null)
                .build();

        StarDetailsDto expected = buildStarDetailsDto(updated);

        when(starRepository.findById(id))
                .thenReturn(Optional.of(existing));
        when(constellationService.getEntity(
                request.getConstellationId(), existing.getConstellation()))
                .thenReturn(existing.getConstellation());
        when(userService.getEntity(request.getAdderId(), existing.getAddedBy()))
                .thenReturn(existing.getAddedBy());
        when(userService.getEntity(request.getVerifierId(), existing.getVerifiedBy()))
                .thenReturn(null);

        StarDetailsDto dto = starService.updateStar(id, request);

        assertEquals(expected, dto);
        verify(starRepository).save(any(Star.class));
    }

    @Test
    void deleteStarGivenValidIdDeletesStar() {
        Star c = buildStar(1, "starName");

        when(starRepository.findById(1))
                .thenReturn(Optional.of(c));

        starService.deleteStar(1);

        verify(starRepository).delete(c);
    }

    @Test
    void starMapperGivenNullStarReturnsNullDtos() {
        assertNull(starMapper.toSummaryDto(null));
        assertNull(starMapper.toDetailsDto(null));
    }
}