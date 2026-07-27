package com.example.SS13.service;

import com.example.SS13.dto.RegisterRequest;
import com.example.SS13.entity.User;
import com.example.SS13.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthServiceRegisterTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void register_Success_PasswordIsEncodedWithBCrypt() {
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .password("plainPassword123")
                .fullName("Nguyen Van Test")
                .build();

        User savedUser = authService.register(request);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("newuser", savedUser.getUsername());
        assertNotEquals("plainPassword123", savedUser.getPassword());
        assertTrue(savedUser.getPassword().startsWith("$2a$") || savedUser.getPassword().startsWith("$2b$"));
        assertTrue(passwordEncoder.matches("plainPassword123", savedUser.getPassword()));
    }

    @Test
    public void register_DuplicateUsername_ThrowsException() {
        RegisterRequest request1 = RegisterRequest.builder()
                .username("dupuser")
                .password("pass123")
                .fullName("User One")
                .build();
        authService.register(request1);

        RegisterRequest request2 = RegisterRequest.builder()
                .username("dupuser")
                .password("pass456")
                .fullName("User Two")
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            authService.register(request2);
        });
    }

    @Test
    public void registerEndpoint_PublicAccess_Returns201Created() throws Exception {
        String jsonRequest = "{\"username\":\"endpointuser\",\"password\":\"mypassword\",\"fullName\":\"Endpoint User\"}";

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("endpointuser"));
    }
}
