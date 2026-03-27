package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.dtos.UserDetailsDto;
import com.sanjayrisbud.starzzboot.dtos.UserDto;
import com.sanjayrisbud.starzzboot.dtos.UserSummaryDto;
import com.sanjayrisbud.starzzboot.exceptions.ResourceNotFoundException;
import com.sanjayrisbud.starzzboot.mappers.UserMapper;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;

import static com.sanjayrisbud.starzzboot.helpers.DtoFactory.*;
import static com.sanjayrisbud.starzzboot.helpers.EntityFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Spy
    private final UserMapper userMapper = new UserMapper();

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "passwordResetSentinel", "resetRequired");
    }

    @Test
    void getEntityGivenNewIdIsNullReturnsNull() {
        User u1 = userService.getEntity(null, null);

        assertNull(u1);

        User u2 = userService.getEntity(null, buildUser());

        assertNull(u2);
    }

    @Test
    void getEntityGivenNullCurrentUserReturnsUserFromRepository() {
        User existing = buildUser();

        when(userRepository.findById(1))
                .thenReturn(Optional.of(existing));

        User u = userService.getEntity(1, null);

        assertEquals(existing, u);
    }

    @Test
    void getEntityGivenCurrentUserMatchesIdReturnsCurrentUser() {
        User current = buildUser(3, "Test");

        User u = userService.getEntity(3, current);

        assertEquals(current, u);
    }

    @Test
    void getEntityGivenCurrentUserDoesNotMatchIdReturnsUserFromRepository() {
        User current = buildUser(3, "Test 3");
        User existing = buildUser(5, "Test 5");

        when(userRepository.findById(5))
                .thenReturn(Optional.of(existing));

        User u = userService.getEntity(5, current);

        assertEquals(existing, u);
        assertNotEquals(current, u);
    }

    @Test
    void getEntityGivenIdNotFoundThrowsResourceNotFoundException() {
        Integer nonExistentId = 999;
        String exceptionMessage = "User with id 999 not found.";
        User current = buildUser(7, "Test 7");

        when(userRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex1 = assertThrows(ResourceNotFoundException.class,
                () -> userService.getEntity(nonExistentId, null));

        assertEquals(exceptionMessage, ex1.getMessage());

        ResourceNotFoundException ex2 = assertThrows(ResourceNotFoundException.class,
                () -> userService.getEntity(nonExistentId, current));

        assertEquals(exceptionMessage, ex2.getMessage());
    }

    @Test
    void getUserListGivenNoUsersFoundReturnsEmptyList() {
        List<User> userList = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(userList);

        List<UserSummaryDto> listing = userService.getUserList();

        assertEquals(0, listing.size());
    }

    @Test
    void getUserListGivenOneUserFoundReturnsListWithOneDto() {
        List<User> userList = new ArrayList<>();
        List<UserSummaryDto> expected = new ArrayList<>();
        User u = buildUser();
        userList.add(u);
        expected.add(buildUserSummaryDto(u));

        when(userRepository.findAll()).thenReturn(userList);

        List<UserSummaryDto> listing = userService.getUserList();

        assertEquals(1, listing.size());
        assertEquals(expected, listing);
    }

    @Test
    void getUserListGivenTwoUsersFoundReturnsListWithTwoDtos() {
        List<User> userList = new ArrayList<>();
        List<UserSummaryDto> expected = new ArrayList<>();
        User u1 = buildUser(1, "Test 1");
        User u2 = buildUser(2, "Test 2");
        userList.add(u1);
        userList.add(u2);
        for (var u : userList)
            expected.add(buildUserSummaryDto(u));

        when(userRepository.findAll()).thenReturn(userList);

        List<UserSummaryDto> listing = userService.getUserList();

        assertEquals(expected.size(), listing.size());
        assertEquals(expected, listing);
    }

    @Test
    void getUserGivenUserIdReturnsUserDetailsDto() {
        User u = buildUser();
        UserDetailsDto expected = buildUserDetailsDto(u);

        when(userRepository.findById(1)).thenReturn(Optional.of(u));

        UserDetailsDto dto = userService.getUser(1);

        assertEquals(expected, dto);
    }

    @Test
    void registerUserGivenCompleteDataAddsNewUser() {
        UserDto request = buildUserDto();

        User u = User.builder()
                .id(10).name(request.getUsername()).email(request.getEmail())
                .firstName(request.getFirstName()).lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        UserDetailsDto expected = buildUserDetailsDto(u);
        // since the id is supposed to be generated by the database, and we are mocking it,
        // we expect the id in the result to be null
        expected.setUserId(null);

        UserDetailsDto dto = userService.registerUser(request);

        assertEquals(expected, dto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserGivenNullFirstNameAndLastNameAddsNewUser() {
        UserDto request = buildUserDto();
        request.setFirstName(null);
        request.setLastName(null);

        User u = User.builder()
                .id(10).name(request.getUsername()).email(request.getEmail())
                .firstName(request.getFirstName()).lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        UserDetailsDto expected = buildUserDetailsDto(u);
        // since the id is supposed to be generated by the database, and we are mocking it,
        // we expect the id in the result to be null
        expected.setUserId(null);

        UserDetailsDto dto = userService.registerUser(request);

        assertEquals(expected, dto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserGivenAllDataChangedUpdatesUserFully() {
        Integer id = 10;
        UserDto request = buildUserDto();

        User existing = buildUser();
        existing.setId(id);

        User updated = User.builder()
                .id(id).name(request.getUsername()).email(request.getEmail())
                .firstName(request.getFirstName()).lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        UserDetailsDto expected = buildUserDetailsDto(updated);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(existing));

        UserDetailsDto dto = userService.updateUser(id, request);

        assertEquals(expected, dto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserGivenSomeDataChangedUpdatesUserPartially() {
        Integer id = 10;
        UserDto request = buildUserDto();
        request.setFirstName("testFirstName");
        request.setLastName("testLastName");

        User existing = buildUser();
        existing.setId(id);
        existing.setFirstName(null);
        existing.setLastName(null);

        // set the request's fields to those "saved" in the database
        request.setUsername(existing.getName());
        request.setEmail(existing.getEmail());
        request.setDateOfBirth(existing.getDateOfBirth());

        User updated = User.builder()
                .id(id).name(existing.getName()).email(existing.getEmail())
                .firstName(request.getFirstName()).lastName(request.getLastName())
                .dateOfBirth(existing.getDateOfBirth())
                .build();

        UserDetailsDto expected = buildUserDetailsDto(updated);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(existing));

        UserDetailsDto dto = userService.updateUser(id, request);

        assertEquals(expected, dto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserGivenNullEmailUpdatesEmailToNull() {
        Integer id = 10;
        UserDto request = buildUserDto();
        request.setEmail(null);

        User existing = buildUser();
        existing.setId(id);

        // set the request's fields to those "saved" in the database
        request.setUsername(existing.getName());
        request.setFirstName(existing.getFirstName());
        request.setLastName(existing.getLastName());
        request.setDateOfBirth(existing.getDateOfBirth());

        User updated = User.builder()
                .id(id).name(existing.getName()).email(null)
                .firstName(existing.getFirstName()).lastName(existing.getLastName())
                .dateOfBirth(existing.getDateOfBirth())
                .build();

        UserDetailsDto expected = buildUserDetailsDto(updated);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(existing));

        UserDetailsDto dto = userService.updateUser(id, request);

        assertEquals(expected, dto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void userMapperGivenNullUserReturnsNullDtos() {
        assertNull(userMapper.toSummaryDto(null));
        assertNull(userMapper.toDetailsDto(null));
    }
}