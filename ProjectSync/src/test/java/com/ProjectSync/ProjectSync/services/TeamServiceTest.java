package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.dtos.TeamDto;
import com.ProjectSync.ProjectSync.dtos.UpdateTeamDto;
import com.ProjectSync.ProjectSync.entities.Team;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.repositories.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    private TeamDto teamDto;
    private UpdateTeamDto updateTeamDto;

    @BeforeEach
    void setUp() {
        teamDto = new TeamDto();
        teamDto.setName("Team 1");

        updateTeamDto = new UpdateTeamDto("Updated Team");
    }

    @Test
    void createOrUpdateTeam_CreateTeam_Success() throws ProjectError {
        // Simula que o time não existe na base de dados
        when(teamRepository.findByName(teamDto.getName())).thenReturn(Optional.empty());

        // Simula a criação de um time
        Team newTeam = new Team();
        newTeam.setName("Team 1");

        when(teamRepository.save(any(Team.class))).thenReturn(newTeam);

        // Chama o serviço
        Team createdTeam = teamService.createOrUpdateEquip(teamDto);

        // Verifica se o time foi criado corretamente
        assertNotNull(createdTeam);
        assertEquals("Team 1", createdTeam.getName());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void createOrUpdateTeam_UpdateTeam_Success() throws ProjectError {
        // Simula que o time já existe na base de dados
        Team existingTeam = new Team();
        existingTeam.setId(1);
        existingTeam.setName("Team 1");

        when(teamRepository.findById(1)).thenReturn(Optional.of(existingTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(existingTeam);

        // Simula o DTO para atualizar
        teamDto.setId(1);
        teamDto.setName("Updated Team");

        // Chama o serviço para atualizar o time
        Team updatedTeam = teamService.createOrUpdateEquip(teamDto);

        // Verifica se o time foi atualizado corretamente
        assertNotNull(updatedTeam);
        assertEquals("Updated Team", updatedTeam.getName());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void createOrUpdateTeam_TeamAlreadyExists_Error() {
        // Simula que o time já existe na base de dados
        when(teamRepository.findByName(teamDto.getName())).thenReturn(Optional.of(new Team()));

        // Verifica se o erro é lançado ao tentar criar um time com o mesmo nome
        assertThrows(ProjectError.class, () -> teamService.createOrUpdateEquip(teamDto));
    }

    @Test
    void createOrUpdateTeam_MissingName_Error() {
        // Simula um TeamDto com nome faltando
        teamDto.setName(null);

        // Verifica se o erro é lançado devido ao nome obrigatório
        assertThrows(ProjectError.class, () -> teamService.createOrUpdateEquip(teamDto));
    }

    @Test
    void updateTeam_Success() throws ProjectError {
        // Simula que o time existe na base de dados
        Team existingTeam = new Team();
        existingTeam.setId(1);
        existingTeam.setName("Team 1");

        when(teamRepository.findById(1)).thenReturn(Optional.of(existingTeam));
        when(teamRepository.save(any(Team.class))).thenReturn(existingTeam);

        // Chama o serviço para atualizar o time
        Team updatedTeam = teamService.upadateTeam(1, updateTeamDto);

        // Verifica se o time foi atualizado corretamente
        assertNotNull(updatedTeam);
        assertEquals("Updated Team", updatedTeam.getName());
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void updateTeam_TeamNotFound_Error() {
        // Simula que o time não existe na base de dados
        when(teamRepository.findById(1)).thenReturn(Optional.empty());

        // Verifica se o erro é lançado quando não encontra o time para atualizar
        assertThrows(ProjectError.class, () -> teamService.upadateTeam(1, updateTeamDto));
    }

    @Test
    void getAllTeams_Success() throws ProjectError {
        // Simula a lista de times
        Team team1 = new Team();
        team1.setName("Team 1");

        Team team2 = new Team();
        team2.setName("Team 2");

        List<Team> teams = Arrays.asList(team1, team2);
        when(teamRepository.findAll()).thenReturn(teams);

        // Chama o serviço para obter todos os times
        List<Team> allTeams = teamService.getAll();

        // Verifica se a lista foi retornada corretamente
        assertNotNull(allTeams);
        assertEquals(2, allTeams.size());
        assertEquals("Team 1", allTeams.get(0).getName());
        assertEquals("Team 2", allTeams.get(1).getName());
        verify(teamRepository, times(1)).findAll();
    }

    @Test
    void getAllTeams_Error() {
        // Simula um erro ao tentar buscar os times
        when(teamRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Verifica se o erro é lançado
        assertThrows(ProjectError.class, () -> teamService.getAll());
    }
}
