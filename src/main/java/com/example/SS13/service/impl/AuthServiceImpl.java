package com.example.SS13.service.impl;

import com.example.SS13.dto.RegisterRequest;
import com.example.SS13.entity.User;
import com.example.SS13.repository.UserRepository;
import com.example.SS13.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SS13.dto.LoginRequest;

import com.example.SS13.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .role("USER")
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User login(LoginRequest request) {
        try {
            // AuthenticationManager tự động gọi CustomUserDetailsService.loadUserByUsername()
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser();
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new IllegalArgumentException("username or password incorrect");
        }
    }
}
