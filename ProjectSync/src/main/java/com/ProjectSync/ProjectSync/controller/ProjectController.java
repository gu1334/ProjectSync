package com.ProjectSync.ProjectSync.controller;

import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping
    public ResponseEntity<Project> createProjectOrUptade(@RequestBody ProjectDto projectDto) throws ProjectError {
        try {
            Project createProject = projectService.createProjectorUpdate(projectDto);

            if (createProject == null || createProject.getName() == null || createProject.getDescription() == null) {
                throw new ProjectError("Erro na criação do projeto: nome ou descrição inválidos");
            }

            if (projectDto.getId() == null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(createProject);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(createProject);
            }
        } catch (ProjectError e) {
            // Log para depuração
            System.err.println("Erro ao criar/atualizar projeto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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
