package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.dtos.ProjectResponse;
import com.ProjectSync.ProjectSync.dtos.UpdateProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.entities.Team;
import com.ProjectSync.ProjectSync.entities.User;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.repositories.ProjectRepository;
import com.ProjectSync.ProjectSync.repositories.TeamRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProjectOrUpdate_CreateProject_Success() throws ProjectError {
        // Dado um ProjectDto para criação
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Test Project");
        projectDto.setDescription("Test Description");
        projectDto.setTeam("Test Team");

        // Simulação de retorno do repositório de equipe
        Team team = new Team();
        team.setName("Test Team");
        when(teamRepository.findByName("Test Team")).thenReturn(Optional.of(team));

        // Simulando o usuário autenticado
        User user = new User();
        when(securityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        // Simulando a criação do projeto
        Project project = new Project();
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Chamada do método
        ProjectResponse response = projectService.createProjectOrUpdate(projectDto);

        // Verifica se o projeto foi criado corretamente
        assertNotNull(response);
        assertEquals("Test Project", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals("Test Team", response.getTeam());
    }

    @Test
    void createProjectOrUpdate_UpdateProject_Success() throws ProjectError {
        // Dado um ProjectDto para atualização
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1);
        projectDto.setName("Updated Project");
        projectDto.setDescription("Updated Description");
        projectDto.setTeam("Test Team");

        // Simulação de retorno do repositório de equipe
        Team team = new Team();
        team.setName("Test Team");
        when(teamRepository.findByName("Test Team")).thenReturn(Optional.of(team));

        // Simulando o projeto existente
        Project existingProject = new Project();
        existingProject.setId(1);
        when(projectRepository.findById(1)).thenReturn(Optional.of(existingProject));

        // Simulando a atualização do projeto
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);

        // Chamada do método
        ProjectResponse response = projectService.createProjectOrUpdate(projectDto);

        // Verifica se o projeto foi atualizado corretamente
        assertNotNull(response);
        assertEquals("Updated Project", response.getName());
        assertEquals("Updated Description", response.getDescription());
        assertEquals("Test Team", response.getTeam());
    }

    @Test
    void createProjectOrUpdate_MissingFields_ThrowsError() {
        // Dado um ProjectDto com campos faltando
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Test Project");
        projectDto.setDescription(null);  // Descrição ausente
        projectDto.setTeam("Test Team");

        // Chamada do método e verificação do erro
        ProjectError thrown = assertThrows(ProjectError.class, () -> {
            projectService.createProjectOrUpdate(projectDto);
        });

        // Verifica se a mensagem de erro é a esperada
        assertEquals("Nome e descrição e time são obrigatórios", thrown.getMessage());
    }



    @Test
    void deleteProject_Success() throws ProjectError {
        // Simulação de um projeto existente
        Project project = new Project();
        project.setId(1);
        User user = new User();
        project.setUser(user);

        // Simulação de autenticação
        when(securityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        // Chamada do método
        projectService.deleteProject(1);

        // Verificação de que o projeto foi deletado
        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    void deleteProject_ProjectNotFound_ThrowsError() {
        // Simulação de projeto inexistente
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        // Chamada do método e verificação do erro
        ProjectError thrown = assertThrows(ProjectError.class, () -> {
            projectService.deleteProject(1);
        });

        // Verifica se a mensagem de erro é a esperada
        assertEquals("Projeto não encontrado", thrown.getMessage());
    }

    @Test
    void deleteProject_UserNotAuthorized_ThrowsError() {
        // Simulação de projeto de outro usuário
        Project project = new Project();
        project.setId(1);
        User differentUser = new User();
        project.setUser(differentUser);

        // Simulação de autenticação de um usuário diferente
        User currentUser = new User();
        when(securityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(currentUser);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        // Chamada do método e verificação do erro
        ProjectError thrown = assertThrows(ProjectError.class, () -> {
            projectService.deleteProject(1);
        });

        // Verifica se a mensagem de erro é a esperada
        assertEquals("Usuario diferente do projeto", thrown.getMessage());
    }
}
