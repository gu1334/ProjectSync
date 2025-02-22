package com.ProjectSync.ProjectSync.controller;

import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<?> createProjectOrUpdate(@RequestBody ProjectDto projectDto) {
        try {
            Project createdProject = projectService.createProjectOrUpdate(projectDto);

            if (createdProject.getName() == null || createdProject.getDescription() == null) {
                throw new ProjectError("Erro na criação do projeto: nome ou descrição inválidos");
            }

            HttpStatus status = (projectDto.getId() == null) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(createdProject);

        } catch (ProjectError e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }



    @GetMapping
    public List<Project> getAllProjects() throws ProjectError {
            return projectService.getAll();
    }




    // Tratamento de exceções de forma separada
    @ExceptionHandler(ProjectError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleProjectError(ProjectError ex) {
        // Aqui você pode personalizar a resposta de erro, por exemplo, retornando uma mensagem.
        return ex.getMessage();
    }

}
