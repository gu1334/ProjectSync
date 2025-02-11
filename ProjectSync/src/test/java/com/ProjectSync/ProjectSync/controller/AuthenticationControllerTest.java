package com.ProjectSync.ProjectSync.controller;

import com.ProjectSync.ProjectSync.dtos.LoginResponse;
import com.ProjectSync.ProjectSync.dtos.LoginUserDto;
import com.ProjectSync.ProjectSync.entities.User;
import com.ProjectSync.ProjectSync.repositories.UserRepository;
import com.ProjectSync.ProjectSync.services.AuthenticationService;
import com.ProjectSync.ProjectSync.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void it_should_return_invalid_credentials_when_authentication_fails() throws Exception {
        // Arrange
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@test.com");
        loginUserDto.setPassword("password");

        Mockito.when(authenticationService.authenticate(Mockito.any(LoginUserDto.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
