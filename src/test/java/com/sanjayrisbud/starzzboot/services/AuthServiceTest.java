package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.config.AdminProperties;
import com.sanjayrisbud.starzzboot.dtos.LoginDto;
import com.sanjayrisbud.starzzboot.exceptions.PasswordResetRequiredException;
import com.sanjayrisbud.starzzboot.models.User;
import com.sanjayrisbud.starzzboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.sanjayrisbud.starzzboot.helpers.EntityFactory.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Spy
    private AdminProperties adminProperties = new AdminProperties();
    @InjectMocks
    private AuthService authService;

    private static final String SENTINEL = "resetSentinel";
    private static final String HASHED_PASSWORD = "hashedPassword";

    @BeforeEach
    void setUp() {
        adminProperties.setAdmins(List.of("admin1"));
        ReflectionTestUtils.setField(authService, "sentinel", SENTINEL);
    }

    @Test
    void loginGivenNonExistentUserThrowsBadCredentialsException() {
        LoginDto request = new LoginDto();
        request.setUsername("nonexistent");
        request.setPassword("password");

        when(userRepository.findByName(request.getUsername())).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void loginGivenWrongPasswordThrowsBadCredentialsException() {
        User user = buildUser(1, "john");
        user.setPassword(HASHED_PASSWORD);

        LoginDto request = new LoginDto();
        request.setUsername("john");
        request.setPassword("wrongPassword");

        when(userRepository.findByName(request.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), HASHED_PASSWORD))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void loginGivenSentinelPasswordThrowsPasswordResetRequiredException() {
        User user = buildUser(1, "john");
        user.setPassword(HASHED_PASSWORD);

        LoginDto request = new LoginDto();
        request.setUsername("john");
        request.setPassword("password");

        when(userRepository.findByName(request.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), HASHED_PASSWORD))
                .thenReturn(true);
        when(passwordEncoder.matches(SENTINEL, HASHED_PASSWORD)).thenReturn(true);

        PasswordResetRequiredException ex = assertThrows(
                PasswordResetRequiredException.class, () -> authService.login(request));
        assertEquals(1, ex.getUserId());
    }

    @Test
    void loginGivenAdminUserReturnsTokenWithAdminRole() {
        User user = buildUser(1, "admin1");
        user.setPassword(HASHED_PASSWORD);

        LoginDto request = new LoginDto();
        request.setUsername("admin1");
        request.setPassword("password");

        when(userRepository.findByName(request.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), HASHED_PASSWORD))
                .thenReturn(true);
        when(passwordEncoder.matches(SENTINEL, HASHED_PASSWORD)).thenReturn(false);
        when(jwtService.generateToken(1, "admin1", "ADMIN"))
                .thenReturn("admin-token");

        assertEquals("admin-token", authService.login(request));
    }

    @Test
    void loginGivenRegularUserReturnsTokenWithUserRole() {
        User user = buildUser(1, "john");
        user.setPassword(HASHED_PASSWORD);

        LoginDto request = new LoginDto();
        request.setUsername("john");
        request.setPassword("password");

        when(userRepository.findByName(request.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), HASHED_PASSWORD))
                .thenReturn(true);
        when(passwordEncoder.matches(SENTINEL, HASHED_PASSWORD)).thenReturn(false);
        when(jwtService.generateToken(1, "john", "USER"))
                .thenReturn("user-token");

        assertEquals("user-token", authService.login(request));
    }
}
