package com.ProjectSync.ProjectSync.controller;

import com.ProjectSync.ProjectSync.dtos.LoginUserDto;
import com.ProjectSync.ProjectSync.dtos.RegisterUserDto;
import com.ProjectSync.ProjectSync.entities.User;
import com.ProjectSync.ProjectSync.repositories.UserRepository;
import com.ProjectSync.ProjectSync.services.AuthenticationService;
import com.ProjectSync.ProjectSync.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
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



    // Unitarios

    @Test
    void it_should_return_invalid_credentials_when_authentication_fails() throws Exception {
        // Arrange
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@test.com");
        loginUserDto.setPassword("password");

        // Simula um erro de autenticação
        Mockito.when(authenticationService.authenticate(Mockito.any(LoginUserDto.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void it_should_return_valid_token_when_authentication_success() throws Exception {
        // Criando um objeto DTO com credenciais válidas
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("test@test.com");
        loginUserDto.setPassword("password");

        // Simulando um token gerado pelo AuthenticationService
        String fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature";

        User mockUser = new User(); // Supondo que exista uma classe User
        Mockito.when(authenticationService.authenticate(Mockito.any(LoginUserDto.class)))
                .thenReturn(mockUser);

        Mockito.when(jwtService.generateToken(Mockito.any(User.class)))
                .thenReturn(fakeToken);

        Mockito.when(jwtService.getExpirationTime())
                .thenReturn(36000L);


        // Fazendo a requisição de login e verificando se retorna um token válido
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isOk()) // Deve retornar status 200
                .andExpect(jsonPath("$.token").exists()) // O JSON deve conter um campo "token"
                .andExpect(jsonPath("$.token").value(fakeToken)); // O token deve ser o esperado
    }


    //Integração

    private LoginUserDto loginUserDto;
    private RegisterUserDto registerUserDto;

    @BeforeEach
    void setUp() {
        registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("teste@.com.r");
        registerUserDto.setPassword("23e23");

        loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("teste@.com.r");
        loginUserDto.setPassword("23e23");
    }

    @Test
    void deveAutenticarUsuarioERetornarToken() throws Exception {
        // Criando usuário primeiro
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDto)))
                .andExpect(status().isOk());

        // Verificar se o usuário foi realmente salvo no banco antes do login
        Thread.sleep(1000); // Dá tempo para persistência no BD (caso esteja assíncrono)

        // Testando login
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expiresIn").exists());
    }


    @Test
    void deveBloquearAcessoAEndpointProtegidoSemToken() throws Exception {
        mockMvc.perform(get("/tarefas")) // Supondo que /tarefas seja um endpoint protegido
                .andExpect(status().isForbidden());
    }





}

