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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;


    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication instanceof UsernamePasswordAuthenticationToken authToken) {
            User user = (User) authToken.getPrincipal(); // Obtém o usuário autenticado
            return user;
        }
        throw new IllegalStateException("Tipo de autenticação inesperado: " + authentication.getClass());
    }


    public ProjectResponse createProjectOrUpdate(ProjectDto projectDto) throws ProjectError {
        if (projectDto.getName() == null || projectDto.getDescription() == null || projectDto.getTeam() == null) {
            throw new ProjectError("Nome e descrição e time são obrigatórios");
        }

        //achar qual o team
        Team team = teamRepository.findByName(projectDto.getTeam()).orElseThrow(() -> new RuntimeException("Equipe não encontrada: "));

        // Atualização de projeto
        if (projectDto.getId() != null) {
            Project existingProject = projectRepository.findById(projectDto.getId()).orElseThrow(() -> new ProjectError("Projeto não encontrado para atualização"));


            existingProject.setName(projectDto.getName());
            existingProject.setDescription(projectDto.getDescription());
            existingProject.setTeam(team);
            projectRepository.save(existingProject);


            ProjectResponse response = new ProjectResponse();
            response.setId(projectDto.getId());
            response.setName(projectDto.getName());
            response.setDescription(projectDto.getDescription());
            response.setTeam(team.getName());
            response.setCreatedAt(existingProject.getCreatedAt());
            return response;
        }

        // Criação de novo projeto
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getUserFromAuthentication(authentication);


        Project newProject = new Project();
        newProject.setTeam(team);
        newProject.setUser(user);
        newProject.setName(projectDto.getName());
        newProject.setDescription(projectDto.getDescription());
        projectRepository.save(newProject);


        ProjectResponse response = new ProjectResponse();
        response.setId(newProject.getId());
        response.setName(projectDto.getName());
        response.setDescription(projectDto.getDescription());
        response.setTeam(team.getName());
        response.setCreatedAt(newProject.getCreatedAt());
        return response;
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
            Project projeto = projectRepository.findById(id).orElseThrow(() -> new ProjectError("Projeto não encontrado"));

            if (!projeto.getUser().getId().equals(user.getId())) {
                throw new ProjectError("Usuario diferente do projeto");
            }
            projectRepository.delete(projeto);

        } catch (Exception e) {
            throw new ProjectError("Erro para deletar projeto: " + e.getMessage());
        }

    }


    // Atualiza um produto (sem atualizar as variações dele)
    public ProjectResponse updateProject(Integer id, UpdateProjectDto updateProjectDto) throws ProjectError {

        try {

            ProjectResponse response = new ProjectResponse();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = getUserFromAuthentication(authentication);

            Project projeto = projectRepository.findById(id).orElseThrow(() -> new ProjectError("Projeto não encontrado"));

            if (!projeto.getUser().getId().equals(user.getId())) {
                throw new ProjectError("Usuario diferente do projeto");
            }

            if (updateProjectDto.name() != null) {
                projeto.setName(updateProjectDto.name());
            }
            if (updateProjectDto.description() != null) {
                projeto.setDescription(updateProjectDto.description());
            }


            if (updateProjectDto.team() != null) {
                //achar qual o team
                Optional<Team> team = teamRepository.findByName(updateProjectDto.team());
                projeto.setTeam(team.orElse(null));

            }
            projectRepository.save(projeto);

            response.setId(projeto.getId());
            response.setName(projeto.getName());
            response.setDescription(projeto.getDescription());
            response.setTeam(projeto.getTeam().getName());
            response.setCreatedAt(projeto.getCreatedAt());

            return response;

        } catch (Exception e) {
            throw new ProjectError("Erro para modificar projeto: " + e.getMessage());
        }
    }


}
