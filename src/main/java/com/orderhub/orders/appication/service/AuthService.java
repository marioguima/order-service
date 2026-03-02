package com.orderhub.orders.appication.service;

import com.orderhub.orders.api.dto.AuthResponse;
import com.orderhub.orders.api.dto.LoginRequest;
import com.orderhub.orders.api.dto.RegisterRequest;
import com.orderhub.orders.domain.exception.BadRquestException;
import com.orderhub.orders.infrastructure.persistence.entity.UserEntity;
import com.orderhub.orders.infrastructure.persistence.repository.UserRepository;
import com.orderhub.orders.infrastructure.security.JwtService;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder= passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse register(RegisterRequest request) throws BadRequestException {

        if (userRepository. existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }

        UserEntity user = new UserEntity(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.name(),
                UserEntity.Role.USER
        );

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user.getEmail(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRquestException("Invalid credentials"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user.getEmail(), user.getName());
    }
}
