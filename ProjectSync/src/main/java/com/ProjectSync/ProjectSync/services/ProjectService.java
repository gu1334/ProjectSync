package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.dtos.UpdateProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.entities.User;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;


    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication instanceof UsernamePasswordAuthenticationToken authToken) {
            User user = (User) authToken.getPrincipal(); // Obtém o usuário autenticado
            return user;
        }
        throw new IllegalStateException("Tipo de autenticação inesperado: " + authentication.getClass());
    }


    public Project createProjectOrUpdate(ProjectDto projectDto) throws ProjectError {
        if (projectDto.getName() == null || projectDto.getDescription() == null) {
            throw new ProjectError("Nome e descrição são obrigatórios");
        }

        // Atualização de projeto
        if (projectDto.getId() != null) {
            Project existingProject = projectRepository.findById(projectDto.getId())
                    .orElseThrow(() -> new ProjectError("Projeto não encontrado para atualização"));

            existingProject.setName(projectDto.getName());
            existingProject.setDescription(projectDto.getDescription());
            return projectRepository.save(existingProject);
        }

        // Criação de novo projeto
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserFromAuthentication(authentication);

        Project newProject = new Project();
        newProject.setUser(user);
        newProject.setName(projectDto.getName());
        newProject.setDescription(projectDto.getDescription());

        return projectRepository.save(newProject);
    }


    public List<Project> getAll(int page, int size) throws ProjectError {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = getUserFromAuthentication(authentication);

            return projectRepository.findByUserId(user.getId(), PageRequest.of(page, size));

        } catch (Exception e) {
            throw new ProjectError("Erro para achar projetos: " + e.getMessage());
        }
    }

    public void deleteProject(Integer id) throws ProjectError {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = getUserFromAuthentication(authentication);
            Project projeto = projectRepository.findById(id)
                    .orElseThrow(() -> new ProjectError("Projeto não encontrado"));

            if (!projeto.getUser().getId().equals(user.getId())) {
                throw new ProjectError("Usuario diferente do projeto");
            }
            projectRepository.delete(projeto);

        } catch (Exception e) {
            throw new ProjectError("Erro para deletar projeto: " + e.getMessage());
        }

    }


    // Atualiza um produto (sem atualizar as variações dele)
    public Project updateProject(Integer id, UpdateProjectDto updateProjectDto) throws ProjectError {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = getUserFromAuthentication(authentication);

            Project projeto = projectRepository.findById(id)
                    .orElseThrow(() -> new ProjectError("Projeto não encontrado"));

            if (!projeto.getUser().getId().equals(user.getId())) {
                throw new ProjectError("Usuario diferente do projeto");
            }

            if (updateProjectDto.name() != null) {
                projeto.setName(updateProjectDto.name());
            }
            if (updateProjectDto.description() != null) {
                projeto.setDescription(updateProjectDto.description());
            }

            return projectRepository.save(projeto);

        } catch (Exception e) {
            throw new ProjectError("Erro para modificar projeto: " + e.getMessage());
        }
    }


}
