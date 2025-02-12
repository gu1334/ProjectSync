package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    private Project project;

    public Project createProjectorUpdate(ProjectDto projectDto) {

        if (projectDto.getId() != null){
            Optional<Project> projectToUpdate = projectRepository.findById(projectDto.getId());

            if (projectToUpdate.isPresent()){
                projectToUpdate.get().setName(projectDto.getProjectName());
                projectToUpdate.get().setDescription(projectDto.getProjectDescription());
                return projectRepository.save(projectToUpdate.get());
            }

        }

            Project project = new Project();
            project.setName(projectDto.getProjectName());
            project.setDescription(projectDto.getProjectDescription());

            return projectRepository.save(project);



    }

}
