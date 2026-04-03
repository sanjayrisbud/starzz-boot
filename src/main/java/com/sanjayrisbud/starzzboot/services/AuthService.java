package com.sanjayrisbud.starzzboot.services;

import com.sanjayrisbud.starzzboot.config.AdminProperties;
import com.sanjayrisbud.starzzboot.dtos.LoginDto;
import com.sanjayrisbud.starzzboot.exceptions.PasswordResetRequiredException;
import com.sanjayrisbud.starzzboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String sentinel;
    private final AdminProperties adminProperties;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       @Value("${app.security.password-reset-sentinel}") String sentinel,
                       AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.sentinel = sentinel;
        this.adminProperties = adminProperties;
    }

    public String login(LoginDto request) {
        var user = userRepository.findByName(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials.");
        }
        if (passwordEncoder.matches(sentinel, user.getPassword())) {
            throw new PasswordResetRequiredException();
        }
        String role = adminProperties.getAdmins().contains(user.getName()) ? "ADMIN" : "USER";
        return jwtService.generateToken(user.getId(), user.getName(), role);
    }
}
