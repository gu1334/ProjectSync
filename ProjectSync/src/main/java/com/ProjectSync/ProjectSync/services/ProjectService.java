package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.configs.JwtAuthenticationFilter;
import com.ProjectSync.ProjectSync.dtos.LoginResponse;
import com.ProjectSync.ProjectSync.dtos.ProjectDto;
import com.ProjectSync.ProjectSync.entities.Project;
import com.ProjectSync.ProjectSync.entities.User;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.repositories.ProjectRepository;
import com.ProjectSync.ProjectSync.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }



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



    public List<Project> getAll() throws ProjectError {
        try {
            return projectRepository.findAll();
        } catch (Exception e) {
            throw new ProjectError("Erro para achar projetos: " + e.getMessage());
        }
    }

}
