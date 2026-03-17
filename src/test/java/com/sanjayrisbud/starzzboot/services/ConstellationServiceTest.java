package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.ConstellationDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationDto;
import com.sanjayrisbud.starzzboot.dtos.ConstellationSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.mappers.ConstellationMapper;
import com.sanjayrisbud.starzzboot.mappers.GalaxyMapper;
import com.sanjayrisbud.starzzboot.mappers.UserMapper;
import com.sanjayrisbud.starzzboot.models.Constellation;
import com.sanjayrisbud.starzzboot.models.Galaxy;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.repositories.ConstellationRepository;
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
class ConstellationServiceTest {
    private final UserMapper userMapper = new UserMapper();
    private final GalaxyMapper galaxyMapper = new GalaxyMapper(userMapper);
    @Spy
    private final ConstellationMapper constellationMapper =
            new ConstellationMapper(galaxyMapper, userMapper);

    @Mock
    private ConstellationRepository constellationRepository;
    @Mock
    private GalaxyService galaxyService;
    @Mock
    private UserService userService;
    @InjectMocks
    private ConstellationService constellationService;

    @Test
    void getEntityGivenNewIdIsNullReturnsNull() {
        Constellation c1 = constellationService.getEntity(null, null);

        assertNull(c1);

        Constellation c2 = constellationService.getEntity(null, buildConstellation());

        assertNull(c2);
    }

    @Test
    void getEntityGivenNullCurrentConstellationReturnsConstellationFromRepository() {
        Constellation existing = buildConstellation();

        when(constellationRepository.findById(1))
                .thenReturn(Optional.of(existing));

        Constellation c = constellationService.getEntity(1, null);

        assertEquals(existing, c);
    }

    @Test
    void getEntityGivenCurrentConstellationMatchesIdReturnsCurrentConstellation() {
        Constellation current = buildConstellation(3, "Test");

        Constellation c = constellationService.getEntity(3, current);

        assertEquals(current, c);
    }

    @Test
    void getEntityGivenCurrentConstellationDoesNotMatchIdReturnsConstellationFromRepository() {
        Constellation current = buildConstellation(3, "Test 3");
        Constellation existing = buildConstellation(5, "Test 5");

        when(constellationRepository.findById(5))
                .thenReturn(Optional.of(existing));

        Constellation c = constellationService.getEntity(5, current);

        assertEquals(existing, c);
        assertNotEquals(current, c);
    }

    @Test
    void getEntityGivenIdNotFoundThrowsResourceNotFoundException() {
        Integer nonExistentId = 999;
        String exceptionMessage = "Constellation with id 999 not found.";
        Constellation current = buildConstellation(7, "Test 7");

        when(constellationRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex1 = assertThrows(ResourceNotFoundException.class,
                () -> constellationService.getEntity(nonExistentId, null));

        assertEquals(exceptionMessage, ex1.getMessage());

        ResourceNotFoundException ex2 = assertThrows(ResourceNotFoundException.class,
                () -> constellationService.getEntity(nonExistentId, current));

        assertEquals(exceptionMessage, ex2.getMessage());
    }

    @Test
    void getConstellationListGivenNoConstellationsFoundReturnsEmptyList() {
        List<Constellation> constellationList = new ArrayList<>();

        when(constellationRepository.findAll()).thenReturn(constellationList);

        List<ConstellationSummaryDto> listing = constellationService.getConstellationList();

        assertEquals(0, listing.size());
    }

    @Test
    void getConstellationListGivenOneConstellationFoundReturnsListWithOneDto() {
        List<Constellation> constellationList = new ArrayList<>();
        List<ConstellationSummaryDto> expected = new ArrayList<>();
        Constellation c = buildConstellation();
        constellationList.add(c);
        expected.add(buildConstellationSummaryDto(c));

        when(constellationRepository.findAll()).thenReturn(constellationList);

        List<ConstellationSummaryDto> listing = constellationService.getConstellationList();

        assertEquals(1, listing.size());
        assertEquals(expected, listing);
    }

    @Test
    void getConstellationListGivenTwoConstellationsFoundReturnsListWithTwoDtos() {
        List<Constellation> constellationList = new ArrayList<>();
        List<ConstellationSummaryDto> expected = new ArrayList<>();
        Constellation c1 = buildConstellation(1, "Test 1");
        Constellation c2 = buildConstellation(2, "Test 2");
        constellationList.add(c1);
        constellationList.add(c2);
        for (var c : constellationList)
            expected.add(buildConstellationSummaryDto(c));

        when(constellationRepository.findAll()).thenReturn(constellationList);

        List<ConstellationSummaryDto> listing = constellationService.getConstellationList();

        assertEquals(expected.size(), listing.size());
        assertEquals(expected, listing);
    }

    @Test
    void getConstellationGivenConstellationIdReturnsConstellationDetailsDto() {
        Galaxy galaxy = buildGalaxy(5, "galaxyName");
        User addedBy = buildUser(10, "addedBy");
        User verifiedBy = buildUser(11, "verifiedBy");
        Constellation c = buildConstellation(1, "constellationName", galaxy, addedBy, verifiedBy);
        ConstellationDetailsDto expected = buildConstellationDetailsDto(c);

        when(constellationRepository.findById(1)).thenReturn(Optional.of(c));

        ConstellationDetailsDto dto = constellationService.getConstellation(1);

        assertEquals(expected, dto);
    }

    @Test
    void registerConstellationGivenCompleteDataAddsNewConstellation() {
        ConstellationDto request = buildConstellationDto();

        Galaxy galaxy = buildGalaxy(request.getGalaxyId(), "galaxyName");
        User addedBy = buildUser(request.getAdderId(), "addedBy");
        User verifiedBy = buildUser(request.getVerifierId(), "verifiedBy");
        Constellation c = buildConstellation(
                10, request.getConstellationName(), galaxy, addedBy, verifiedBy);

        ConstellationDetailsDto expected = buildConstellationDetailsDto(c);
        // since the id is supposed to be generated by the database, and we are mocking it,
        // we expect the id in the result to be null
        expected.setConstellationId(null);

        when(galaxyService.getEntity(request.getGalaxyId(), null))
                .thenReturn(galaxy);
        when(userService.getEntity(request.getAdderId(), null))
                .thenReturn(addedBy);
        when(userService.getEntity(request.getVerifierId(), null))
                .thenReturn(verifiedBy);

        ConstellationDetailsDto dto = constellationService.registerConstellation(request);

        assertEquals(expected, dto);
        verify(constellationRepository).save(any(Constellation.class));
    }

    @Test
    void registerConstellationGivenNullVerifierAddsNewConstellation() {
        ConstellationDto request = buildConstellationDto();
        request.setVerifierId(null);

        Galaxy galaxy = buildGalaxy(request.getGalaxyId(), "galaxyName");
        User addedBy = buildUser(request.getAdderId(), "addedBy");
        Constellation c = buildConstellation(
                20, request.getConstellationName(), galaxy, addedBy, null);

        ConstellationDetailsDto expected = buildConstellationDetailsDto(c);
        // since the id is supposed to be generated by the database, and we are mocking it,
        // we expect the id in the result to be null
        expected.setConstellationId(null);

        when(galaxyService.getEntity(request.getGalaxyId(), null))
                .thenReturn(galaxy);
        when(userService.getEntity(request.getAdderId(), null))
                .thenReturn(addedBy);
        when(userService.getEntity(request.getVerifierId(), null))
                .thenReturn(null);

        ConstellationDetailsDto dto = constellationService.registerConstellation(request);

        assertEquals(expected, dto);
        verify(constellationRepository).save(any(Constellation.class));
    }

    @Test
    void updateConstellationGivenAllDataChangedUpdatesConstellationFully() {
        Integer id = 10;
        ConstellationDto request = buildConstellationDto();

        Constellation existing = buildConstellation();
        existing.setId(id);
        Galaxy galaxy = buildGalaxy(request.getGalaxyId(), "galaxyName");
        User addedBy = buildUser(request.getAdderId(), "addedBy");
        User verifiedBy = buildUser(request.getVerifierId(), "verifiedBy");
        Constellation updated = buildConstellation(
                id, request.getConstellationName(), galaxy, addedBy, verifiedBy);

        ConstellationDetailsDto expected = buildConstellationDetailsDto(updated);

        when(constellationRepository.findById(id))
                .thenReturn(Optional.of(existing));
        when(galaxyService.getEntity(request.getGalaxyId(), existing.getGalaxy()))
                .thenReturn(galaxy);
        when(userService.getEntity(request.getAdderId(), existing.getAddedBy()))
                .thenReturn(addedBy);
        when(userService.getEntity(request.getVerifierId(), existing.getVerifiedBy()))
                .thenReturn(verifiedBy);

        ConstellationDetailsDto dto = constellationService.updateConstellation(id, request);

        assertEquals(expected, dto);
        verify(constellationRepository).save(any(Constellation.class));
    }

    @Test
    void updateConstellationGivenSomeDataChangedUpdatesConstellationPartially() {
        Integer id = 10;
        ConstellationDto request = buildConstellationDto();
        request.setConstellationName("testConstellation");
        request.setVerifierId(34);

        Constellation existing = buildConstellation();
        existing.setId(id);
        existing.setVerifiedBy(null);

        // set the request's fields to those "saved" in the database
        request.setGalaxyId(existing.getGalaxy().getId());
        request.setAdderId(existing.getAddedBy().getId());

        User verifiedBy = buildUser(request.getVerifierId(), "verifiedBy");
        Constellation updated = buildConstellation(
                id, request.getConstellationName(), existing.getGalaxy(),
                existing.getAddedBy(), verifiedBy);

        ConstellationDetailsDto expected = buildConstellationDetailsDto(updated);

        when(constellationRepository.findById(id))
                .thenReturn(Optional.of(existing));
        when(galaxyService.getEntity(request.getGalaxyId(), existing.getGalaxy()))
                .thenReturn(existing.getGalaxy());
        when(userService.getEntity(request.getAdderId(), existing.getAddedBy()))
                .thenReturn(existing.getAddedBy());
        when(userService.getEntity(request.getVerifierId(), existing.getVerifiedBy()))
                .thenReturn(verifiedBy);

        ConstellationDetailsDto dto = constellationService.updateConstellation(id, request);

        assertEquals(expected, dto);
        verify(constellationRepository).save(any(Constellation.class));
    }

    @Test
    void updateConstellationGivenNullVerifierUpdatesVerifiedByToNull() {
        Integer id = 10;
        ConstellationDto request = buildConstellationDto();
        request.setVerifierId(null);

        Constellation existing = buildConstellation();
        existing.setId(id);

        // set the request's fields to those "saved" in the database
        request.setConstellationName(existing.getName());
        request.setGalaxyId(existing.getGalaxy().getId());
        request.setAdderId(existing.getAddedBy().getId());

        Constellation updated = buildConstellation(
                id, existing.getName(), existing.getGalaxy(),
                existing.getAddedBy(), null);

        ConstellationDetailsDto expected = buildConstellationDetailsDto(updated);

        when(constellationRepository.findById(id))
                .thenReturn(Optional.of(existing));
        when(galaxyService.getEntity(request.getGalaxyId(), existing.getGalaxy()))
                .thenReturn(existing.getGalaxy());
        when(userService.getEntity(request.getAdderId(), existing.getAddedBy()))
                .thenReturn(existing.getAddedBy());
        when(userService.getEntity(request.getVerifierId(), existing.getVerifiedBy()))
                .thenReturn(null);

        ConstellationDetailsDto dto = constellationService.updateConstellation(id, request);

        assertEquals(expected, dto);
        verify(constellationRepository).save(any(Constellation.class));
    }

    @Test
    void deleteConstellationGivenValidIdDeletesConstellation() {
        Constellation c = buildConstellation(1, "constellationName");

        when(constellationRepository.findById(1))
                .thenReturn(Optional.of(c));

        constellationService.deleteConstellation(1);

        verify(constellationRepository).delete(c);
    }

    @Test
    void constellationMapperGivenNullConstellationReturnsNullDtos() {
        assertNull(constellationMapper.toSummaryDto(null));
        assertNull(constellationMapper.toDetailsDto(null));
    }
}