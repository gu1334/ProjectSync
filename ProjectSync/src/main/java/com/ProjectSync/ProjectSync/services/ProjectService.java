package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    private Project project;

    public Project createProjectorUpdate(ProjectDto projectDto) throws ProjectError {
        try {
            if (projectDto.getId() != null) {
                try {
                    Optional<Project> projectToUpdate = projectRepository.findById(projectDto.getId());

                    if (projectToUpdate.isPresent()) {
                        projectToUpdate.get().setName(projectDto.getProjectName());
                        projectToUpdate.get().setDescription(projectDto.getProjectDescription());
                        return projectRepository.save(projectToUpdate.get());
                    }
                }catch (Exception e) {
                    throw new ProjectError("Error updating project: " + e.getMessage());
                }

            }

            Project project = new Project();
            project.setName(projectDto.getProjectName());
            project.setDescription(projectDto.getProjectDescription());

            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectError("Error updating project: " + e.getMessage());
        }
    }

}
