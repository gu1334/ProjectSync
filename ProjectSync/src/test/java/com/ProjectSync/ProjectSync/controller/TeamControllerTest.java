package com.ProjectSync.ProjectSync.controller;

import com.ProjectSync.ProjectSync.dtos.TeamDto;
import com.ProjectSync.ProjectSync.dtos.UpdateTeamDto;
import com.ProjectSync.ProjectSync.entities.Team;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.services.TeamService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TeamControllerTest {

    @MockBean
    private TeamService teamService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createOrUpdateTeam_CreateTeam_Success() throws Exception {
        // Dado um TeamDto válido para criação
        TeamDto teamDto = new TeamDto();
        teamDto.setName("New Team");

        // Simulação de retorno da criação do time
        Team createdTeam = new Team();
        createdTeam.setName("New Team");

        when(teamService.createOrUpdateEquip(teamDto)).thenReturn(createdTeam);

        // Chamada do método HTTP
        MvcResult result = mockMvc.perform(post("/teams")
                        .contentType("application/json")
                        .content("{\"name\": \"New Team\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Team"))
                .andReturn();

        // Verifica o corpo da resposta
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void createOrUpdateTeam_UpdateTeam_Success() throws Exception {
        // Dado um TeamDto válido para atualização
        TeamDto teamDto = new TeamDto();
        teamDto.setId(1);
        teamDto.setName("Updated Team");

        // Simulação de retorno da atualização do time
        Team updatedTeam = new Team();
        updatedTeam.setId(1);
        updatedTeam.setName("Updated Team");

        when(teamService.createOrUpdateEquip(teamDto)).thenReturn(updatedTeam);

        // Chamada do método HTTP
        MvcResult result = mockMvc.perform(post("/teams")
                        .contentType("application/json")
                        .content("{\"id\": 1, \"name\": \"Updated Team\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Team"))
                .andReturn();

        // Verifica o corpo da resposta
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    void createOrUpdateTeam_ErrorDuringCreation() throws Exception {
        // Dado um TeamDto inválido para criar o time
        TeamDto teamDto = new TeamDto();
        teamDto.setName(null); // Nome ausente

        // Simulação de erro durante a criação
        when(teamService.createOrUpdateEquip(teamDto)).thenThrow(new ProjectError("Erro na criação do time"));

        // Chamada do método HTTP e verificação de erro
        mockMvc.perform(post("/teams")
                        .contentType("application/json")
                        .content("{\"name\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ProjectError));
    }





    @Test
    void getAllTeams_Success() throws Exception {
        // Simulação de retorno de times
        Team team1 = new Team();
        team1.setName("Team 1");

        Team team2 = new Team();
        team2.setName("Team 2");

        when(teamService.getAll()).thenReturn(Arrays.asList(team1, team2));

        // Chamada do método HTTP
        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Team 1"))
                .andExpect(jsonPath("$[1].name").value("Team 2"));
    }

    @Test
    void getAllTeams_Error() throws Exception {
        // Simulação de erro ao buscar todos os times
        when(teamService.getAll()).thenThrow(new ProjectError("Erro ao buscar times"));

        // Chamada do método HTTP e verificação de erro
        mockMvc.perform(get("/teams"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ProjectError));
    }
}
