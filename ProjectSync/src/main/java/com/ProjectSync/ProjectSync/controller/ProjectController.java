package com.ProjectSync.ProjectSync.controller;

import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.dtos.UpdateProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<Project> getAllProjects(@RequestParam int page,
                                        @RequestParam int size) throws ProjectError {
        try {

            return projectService.getAll(page, size);
        } catch (Exception e) {
            throw new ProjectError(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") Integer id) throws ProjectError {

        try {
            projectService.deleteProject(id);
            return ResponseEntity.status(HttpStatus.OK).body("Projeto deletado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erro ao excluir o projeto");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") Integer id, @RequestBody UpdateProjectDto updateProjectDto)throws ProjectError{
        try {

            Project updateProject = projectService.updateProject(id, updateProjectDto);
            return ResponseEntity.status(HttpStatus.OK).body(updateProject);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }



    // Tratamento de exceções de forma separada
    @ExceptionHandler(ProjectError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleProjectError(ProjectError ex) {
        // Aqui você pode personalizar a resposta de erro, por exemplo, retornando uma mensagem.
        return ex.getMessage();
    }

}
