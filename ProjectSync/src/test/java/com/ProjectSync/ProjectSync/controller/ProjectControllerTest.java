package com.ProjectSync.ProjectSync.controller;

import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.repositories.ProjectRepository;
import com.ProjectSync.ProjectSync.services.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private ProjectRepository projectRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createProject() throws Exception {
        // Dado um ProjectDto
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Test Project");
        projectDto.setDescription("Project description");

        Project createdProject = new Project();
        createdProject.setName("Test Project");
        createdProject.setDescription("Project description");

        when(projectService.createProjectorUpdate(Mockito.any(ProjectDto.class))).thenReturn(createdProject);

        String projectDtoJson = objectMapper.writeValueAsString(projectDto);

        // Chamada POST
        mockMvc.perform(post("http://localhost:8080/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectDtoJson))
                .andExpect(status().isCreated())  // Verifica o status 201
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Project description"));
    }

    @Test
    void updateProject() throws Exception {
        // Dado um ProjectDto com ID
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1);
        projectDto.setName("Updated Project");
        projectDto.setDescription("Updated description");

        Project updatedProject = new Project();
        updatedProject.setId(1);
        updatedProject.setName("Updated Project");
        updatedProject.setDescription("Updated description");

        when(projectService.createProjectorUpdate(Mockito.any(ProjectDto.class))).thenReturn(updatedProject);

        String projectDtoJson = objectMapper.writeValueAsString(projectDto);

        // Chamada POST
        mockMvc.perform(post("http://localhost:8080/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectDtoJson))
                .andExpect(status().isOk())  // Verifica o status 200
                .andExpect(jsonPath("$.name").value("Updated Project"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void projectWithNullNameOrDescription() throws Exception {
        // Arrange: Projeto com nome e descrição inválidos
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName(null);
        projectDto.setDescription(null);

        when(projectService.createProjectorUpdate(Mockito.any(ProjectDto.class)))
                .thenThrow(new ProjectError("Campos Obrigatórios"));

        // Act & Assert
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Campos Obrigatórios"));
    }

    @Test
    void obrigatory_camps_description() throws Exception {
        // Arrange: Só descrição
        ProjectDto projectDto = new ProjectDto();
        projectDto.setDescription("teste obrigatorio");

        when(projectService.createProjectorUpdate(Mockito.any(ProjectDto.class)))
                .thenThrow(new ProjectError("Campos Obrigatórios"));

        // Act & Assert
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obrigatory_camps_name() throws Exception {
        // Arrange: Só nome
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("teste obrigatorio");

        when(projectService.createProjectorUpdate(Mockito.any(ProjectDto.class)))
                .thenThrow(new ProjectError("Campos Obrigatórios"));

        // Act & Assert
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testProjectError() throws Exception {
        // Arrange: Exceção ProjectError
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Erro Project");
        projectDto.setDescription("Erro na criação");

        when(projectService.createProjectorUpdate(Mockito.any(ProjectDto.class)))
                .thenThrow(new ProjectError("Erro na criação do projeto"));

        // Act & Assert
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro na criação do projeto"));
    }
}
