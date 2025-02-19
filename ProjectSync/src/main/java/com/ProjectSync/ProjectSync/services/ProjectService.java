package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.entities.User;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;


    public Project createProjectorUpdate(ProjectDto projectDto) throws ProjectError {
        try {
            if (projectDto.getId() != null) {
                Optional<Project> projectToUpdate = projectRepository.findById(projectDto.getId());

                if (projectToUpdate.isPresent()) {
                    projectToUpdate.get().setName(projectDto.getName());
                    projectToUpdate.get().setDescription(projectDto.getDescription());
                    return projectRepository.save(projectToUpdate.get());
                } else {
                    throw new ProjectError("Projeto não encontrado para atualização");
                }
            }

            if (projectDto.getName() == null || projectDto.getDescription() == null) {
                throw new ProjectError("Nome e descrição são obrigatórios");
            }


            Project project = new Project();
            project.setName(projectDto.getName());
            project.setDescription(projectDto.getDescription());


            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectError("Erro ao criar ou atualizar o projeto: " + e.getMessage());
        }
    }


    public List<Project> getAll() throws ProjectError {
        try {
            return projectRepository.findAll();
        }catch (Exception e){
            throw new ProjectError("Erro para achar projetos: " + e.getMessage());
        }
    }

}
