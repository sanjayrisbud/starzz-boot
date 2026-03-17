package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.GalaxyDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxyDto;
import com.sanjayrisbud.starzzboot.dtos.GalaxySummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.mappers.GalaxyMapper;
import com.sanjayrisbud.starzzboot.mappers.UserMapper;
import com.sanjayrisbud.starzzboot.models.Galaxy;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.repositories.GalaxyRepository;
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
class GalaxyServiceTest {
    private final UserMapper userMapper = new UserMapper();
    @Spy
    private final GalaxyMapper galaxyMapper = new GalaxyMapper(userMapper);

    @Mock
    private GalaxyRepository galaxyRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private GalaxyService galaxyService;

    @Test
    void getEntityGivenNewIdIsNullReturnsNull() {
        Galaxy g1 = galaxyService.getEntity(null, null);

        assertNull(g1);

        Galaxy g2 = galaxyService.getEntity(null, buildGalaxy());

        assertNull(g2);
    }

    @Test
    void getEntityGivenNullCurrentGalaxyReturnsGalaxyFromRepository() {
        Galaxy existing = buildGalaxy();

        when(galaxyRepository.findById(1))
                .thenReturn(Optional.of(existing));

        Galaxy c = galaxyService.getEntity(1, null);

        assertEquals(existing, c);
    }

    @Test
    void getEntityGivenCurrentGalaxyMatchesIdReturnsCurrentGalaxy() {
        Galaxy current = buildGalaxy(3, "Test");

        Galaxy g = galaxyService.getEntity(3, current);

        assertEquals(current, g);
    }

    @Test
    void getEntityGivenCurrentGalaxyDoesNotMatchIdReturnsGalaxyFromRepository() {
        Galaxy current = buildGalaxy(3, "Test 3");
        Galaxy existing = buildGalaxy(5, "Test 5");

        when(galaxyRepository.findById(5))
                .thenReturn(Optional.of(existing));

        Galaxy g = galaxyService.getEntity(5, current);

        assertEquals(existing, g);
        assertNotEquals(current, g);
    }

    @Test
    void getEntityGivenIdNotFoundThrowsResourceNotFoundException() {
        Integer nonExistentId = 999;
        String exceptionMessage = "Galaxy with id 999 not found.";
        Galaxy current = buildGalaxy(7, "Test 7");

        when(galaxyRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex1 = assertThrows(ResourceNotFoundException.class,
                () -> galaxyService.getEntity(nonExistentId, null));

        assertEquals(exceptionMessage, ex1.getMessage());

        ResourceNotFoundException ex2 = assertThrows(ResourceNotFoundException.class,
                () -> galaxyService.getEntity(nonExistentId, current));

        assertEquals(exceptionMessage, ex2.getMessage());
    }

    @Test
    void getGalaxyListGivenNoGalaxiesFoundReturnsEmptyList() {
        List<Galaxy> galaxyList = new ArrayList<>();

        when(galaxyRepository.findAll()).thenReturn(galaxyList);

        List<GalaxySummaryDto> listing = galaxyService.getGalaxyList();

        assertEquals(0, listing.size());
    }

    @Test
    void getGalaxyListGivenOneGalaxyFoundReturnsListWithOneDto() {
        List<Galaxy> galaxyList = new ArrayList<>();
        List<GalaxySummaryDto> expected = new ArrayList<>();
        Galaxy g = buildGalaxy();
        galaxyList.add(g);
        expected.add(buildGalaxySummaryDto(g));

        when(galaxyRepository.findAll()).thenReturn(galaxyList);

        List<GalaxySummaryDto> listing = galaxyService.getGalaxyList();

        assertEquals(1, listing.size());
        assertEquals(expected, listing);
    }

    @Test
    void getGalaxyListGivenTwoGalaxiesFoundReturnsListWithTwoDtos() {
        List<Galaxy> galaxyList = new ArrayList<>();
        List<GalaxySummaryDto> expected = new ArrayList<>();
        Galaxy g1 = buildGalaxy(1, "Test 1");
        Galaxy g2 = buildGalaxy(2, "Test 2");
        galaxyList.add(g1);
        galaxyList.add(g2);
        for (var g : galaxyList)
            expected.add(buildGalaxySummaryDto(g));

        when(galaxyRepository.findAll()).thenReturn(galaxyList);

        List<GalaxySummaryDto> listing = galaxyService.getGalaxyList();

        assertEquals(expected.size(), listing.size());
        assertEquals(expected, listing);
    }

    @Test
    void getGalaxyGivenGalaxyIdReturnsGalaxyDetailsDto() {
        User addedBy = buildUser(10, "addedBy");
        User verifiedBy = buildUser(11, "verifiedBy");
        Galaxy g = buildGalaxy(1, "galaxyName", addedBy, verifiedBy);
        GalaxyDetailsDto expected = buildGalaxyDetailsDto(g);

        when(galaxyRepository.findById(1)).thenReturn(Optional.of(g));

        GalaxyDetailsDto dto = galaxyService.getGalaxy(1);

        assertEquals(expected, dto);
    }

    @Test
    void registerGalaxyGivenCompleteDataAddsNewGalaxy() {
        GalaxyDto request = buildGalaxyDto();

        User addedBy = buildUser(request.getAdderId(), "addedBy");
        User verifiedBy = buildUser(request.getVerifierId(), "verifiedBy");
        Galaxy g = Galaxy.builder()
                .id(10).name(request.getGalaxyName()).type(request.getGalaxyType())
                .distanceMly(request.getDistanceMly()).redshift(request.getRedshift())
                .massSolar(request.getMassSolar()).diameterLy(request.getDiameterLy())
                .addedBy(addedBy).verifiedBy(verifiedBy)
                .build();

        GalaxyDetailsDto expected = buildGalaxyDetailsDto(g);
        // since the id is supposed to be generated by the database, and we are mocking it,
        // we expect the id in the result to be null
        expected.setGalaxyId(null);

        when(userService.getEntity(request.getAdderId(), null))
                .thenReturn(addedBy);
        when(userService.getEntity(request.getVerifierId(), null))
                .thenReturn(verifiedBy);

        GalaxyDetailsDto dto = galaxyService.registerGalaxy(request);

        assertEquals(expected, dto);
        verify(galaxyRepository).save(any(Galaxy.class));
    }

    @Test
    void registerGalaxyGivenNullVerifierAddsNewGalaxy() {
        GalaxyDto request = buildGalaxyDto();
        request.setVerifierId(null);

        User addedBy = buildUser(request.getAdderId(), "addedBy");
        Galaxy g = Galaxy.builder()
                .id(10).name(request.getGalaxyName()).type(request.getGalaxyType())
                .distanceMly(request.getDistanceMly()).redshift(request.getRedshift())
                .massSolar(request.getMassSolar()).diameterLy(request.getDiameterLy())
                .addedBy(addedBy).verifiedBy(null)
                .build();

        GalaxyDetailsDto expected = buildGalaxyDetailsDto(g);
        // since the id is supposed to be generated by the database, and we are mocking it,
        // we expect the id in the result to be null
        expected.setGalaxyId(null);

        when(userService.getEntity(request.getAdderId(), null))
                .thenReturn(addedBy);
        when(userService.getEntity(request.getVerifierId(), null))
                .thenReturn(null);

        GalaxyDetailsDto dto = galaxyService.registerGalaxy(request);

        assertEquals(expected, dto);
        verify(galaxyRepository).save(any(Galaxy.class));
    }

    @Test
    void updateGalaxyGivenAllDataChangedUpdatesGalaxyFully() {
        Integer id = 10;
        GalaxyDto request = buildGalaxyDto();

        Galaxy existing = buildGalaxy();
        existing.setId(id);
        User addedBy = buildUser(request.getAdderId(), "addedBy");
        User verifiedBy = buildUser(request.getVerifierId(), "verifiedBy");
        Galaxy updated = Galaxy.builder()
                .id(id).name(request.getGalaxyName()).type(request.getGalaxyType())
                .distanceMly(request.getDistanceMly()).redshift(request.getRedshift())
                .massSolar(request.getMassSolar()).diameterLy(request.getDiameterLy())
                .addedBy(addedBy).verifiedBy(verifiedBy)
                .build();

        GalaxyDetailsDto expected = buildGalaxyDetailsDto(updated);

        when(galaxyRepository.findById(id))
                .thenReturn(Optional.of(existing));
        when(userService.getEntity(request.getAdderId(), existing.getAddedBy()))
                .thenReturn(addedBy);
        when(userService.getEntity(request.getVerifierId(), existing.getVerifiedBy()))
                .thenReturn(verifiedBy);

        GalaxyDetailsDto dto = galaxyService.updateGalaxy(id, request);

        assertEquals(expected, dto);
        verify(galaxyRepository).save(any(Galaxy.class));
    }

    @Test
    void updateGalaxyGivenSomeDataChangedUpdatesGalaxyPartially() {
        Integer id = 10;
        GalaxyDto request = buildGalaxyDto();
        request.setGalaxyName("testGalaxy");
        request.setGalaxyType("testGalaxyType");
        request.setDistanceMly(40);
        request.setRedshift(10);
        request.setVerifierId(34);

        Galaxy existing = buildGalaxy();
        existing.setId(id);
        existing.setVerifiedBy(null);

        // set the request's fields to those "saved" in the database
        request.setMassSolar(existing.getMassSolar());
        request.setDiameterLy(existing.getDiameterLy());
        request.setAdderId(existing.getAddedBy().getId());

        User verifiedBy = buildUser(request.getVerifierId(), "verifiedBy");
        Galaxy updated = Galaxy.builder()
                .id(id).name(request.getGalaxyName()).type(request.getGalaxyType())
                .distanceMly(request.getDistanceMly()).redshift(request.getRedshift())
                .massSolar(existing.getMassSolar()).diameterLy(existing.getDiameterLy())
                .addedBy(existing.getAddedBy()).verifiedBy(verifiedBy)
                .build();

        GalaxyDetailsDto expected = buildGalaxyDetailsDto(updated);

        when(galaxyRepository.findById(id))
                .thenReturn(Optional.of(existing));
        when(userService.getEntity(request.getAdderId(), existing.getAddedBy()))
                .thenReturn(existing.getAddedBy());
        when(userService.getEntity(request.getVerifierId(), existing.getVerifiedBy()))
                .thenReturn(verifiedBy);

        GalaxyDetailsDto dto = galaxyService.updateGalaxy(id, request);

        assertEquals(expected, dto);
        verify(galaxyRepository).save(any(Galaxy.class));
    }

    @Test
    void updateGalaxyGivenNullVerifierUpdatesVerifiedByToNull() {
        Integer id = 10;
        GalaxyDto request = buildGalaxyDto();
        request.setVerifierId(null);

        Galaxy existing = buildGalaxy();
        existing.setId(id);

        // set the request's fields to those "saved" in the database
        request.setGalaxyName(existing.getName());
        request.setGalaxyType(existing.getType());
        request.setDistanceMly(existing.getDistanceMly());
        request.setRedshift(existing.getRedshift());
        request.setMassSolar(existing.getMassSolar());
        request.setDiameterLy(existing.getDiameterLy());
        request.setAdderId(existing.getAddedBy().getId());

        Galaxy updated = Galaxy.builder()
                .id(id).name(existing.getName()).type(existing.getType())
                .distanceMly(existing.getDistanceMly()).redshift(existing.getRedshift())
                .massSolar(existing.getMassSolar()).diameterLy(existing.getDiameterLy())
                .addedBy(existing.getAddedBy()).verifiedBy(null)
                .build();

        GalaxyDetailsDto expected = buildGalaxyDetailsDto(updated);

        when(galaxyRepository.findById(id))
                .thenReturn(Optional.of(existing));
        when(userService.getEntity(request.getAdderId(), existing.getAddedBy()))
                .thenReturn(existing.getAddedBy());
        when(userService.getEntity(request.getVerifierId(), existing.getVerifiedBy()))
                .thenReturn(null);

        GalaxyDetailsDto dto = galaxyService.updateGalaxy(id, request);

        assertEquals(expected, dto);
        verify(galaxyRepository).save(any(Galaxy.class));
    }

    @Test
    void deleteGalaxyGivenValidIdDeletesGalaxy() {
        Galaxy c = buildGalaxy(1, "galaxyName");

        when(galaxyRepository.findById(1))
                .thenReturn(Optional.of(c));

        galaxyService.deleteGalaxy(1);

        verify(galaxyRepository).delete(c);
    }

    @Test
    void galaxyMapperGivenNullGalaxyReturnsNullDtos() {
        assertNull(galaxyMapper.toSummaryDto(null));
        assertNull(galaxyMapper.toDetailsDto(null));
    }
}