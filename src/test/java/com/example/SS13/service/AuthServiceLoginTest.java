package com.example.SS13.service;

import com.example.SS13.dto.LoginRequest;
import com.example.SS13.dto.RegisterRequest;
import com.example.SS13.entity.User;
import com.example.SS13.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthServiceLoginTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        authService.register(RegisterRequest.builder()
                .username("validuser")
                .password("correctpass123")
                .fullName("Valid User")
                .build());
    }

    @Test
    public void login_Success_ReturnsUser() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("validuser")
                .password("correctpass123")
                .build();

        User user = authService.login(loginRequest);

        assertNotNull(user);
        assertEquals("validuser", user.getUsername());
    }

    @Test
    public void login_WrongPassword_ThrowsException() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("validuser")
                .password("wrongpassword")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("username or password incorrect", exception.getMessage());
    }

    @Test
    public void login_WrongUsername_ThrowsException() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("nonexistent")
                .password("correctpass123")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("username or password incorrect", exception.getMessage());
    }

    @Test
    public void loginEndpoint_WrongCredentials_Returns401Unauthorized() throws Exception {
        String jsonRequest = "{\"username\":\"validuser\",\"password\":\"wrongpass\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("username or password incorrect"));
    }

    @Test
    public void loginEndpoint_CorrectCredentials_Returns200OK() throws Exception {
        String jsonRequest = "{\"username\":\"validuser\",\"password\":\"correctpass123\"}";

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("validuser"));
    }
}
