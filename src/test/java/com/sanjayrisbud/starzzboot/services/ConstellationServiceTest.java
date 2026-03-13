package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.models.Constellation;
import com.sanjayrisbud.starzzboot.repositories.ConstellationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConstellationServiceTest {
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

        Constellation c2 = constellationService.getEntity(null,
                Constellation.builder().build());
        assertNull(c2);
    }

    @Test
    void getEntityGivenNullCurrentConstellationReturnsConstellationFromRepository() {
        Constellation existingConstellation = Constellation.builder().build();
        when(constellationRepository.findById(1))
                .thenReturn(Optional.of(existingConstellation));

        Constellation c = constellationService.getEntity(1,null);

        assertEquals(existingConstellation, c);
    }

    @Test
    void getEntityGivenCurrentConstellationMatchesIdReturnsCurrentConstellation() {
        Constellation currentConstellation = Constellation.builder().id(5).build();
        Constellation c = constellationService.getEntity(5, currentConstellation);
        assertEquals(currentConstellation, c);
    }

    @Test
    void getEntityGivenCurrentConstellationDoesNotMatchIdReturnsConstellationFromRepository() {
        Constellation currentConstellation = Constellation.builder().id(7).build();
        Constellation existingConstellation = Constellation.builder().id(3).build();
        when(constellationRepository.findById(3))
                .thenReturn(Optional.of(existingConstellation));

        Constellation c = constellationService.getEntity(3, currentConstellation);

        assertEquals(existingConstellation, c);
        assertNotEquals(currentConstellation, c);
    }

    @Test
    void getEntityGivenIdNotFoundThrowsResourceNotFoundException() {
        Integer nonExistentId = 999;
        String exceptionMessage = "Constellation with id 999 not found.";
        Constellation currentConstellation = Constellation.builder().id(8).build();

        when(constellationRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex1 = assertThrows(ResourceNotFoundException.class,
                () -> constellationService.getEntity(nonExistentId, null));
        assertEquals(exceptionMessage, ex1.getMessage());

        ResourceNotFoundException ex2 = assertThrows(ResourceNotFoundException.class,
                () -> constellationService.getEntity(nonExistentId, currentConstellation));
        assertEquals(exceptionMessage, ex2.getMessage());

    }

}