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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Value("${spring.kafka.topic.bora-praticar}")
    private String kafkaTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public ProjectService(KafkaTemplate<String, String> kafkaTemplate, ProjectRepository projectRepository, TeamRepository teamRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
    }


    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new IllegalStateException("Usuário não autenticado ou inválido");
    }


    public ProjectResponse createProjectOrUpdate(ProjectDto projectDto) throws ProjectError {
        if (projectDto.getName() == null || projectDto.getDescription() == null || projectDto.getTeam() == null) {
            throw new ProjectError("Nome, descrição e equipe são obrigatórios");
        }

        ProjectResponse response = new ProjectResponse();
        //achar qual o team
        Team team = teamRepository.findByName(projectDto.getTeam())
                .orElseThrow(() -> new ProjectError("Equipe não encontrada: " + projectDto.getTeam()));

        // Atualização de projeto
        if (projectDto.getId() != null) {
            Project existingProject = projectRepository.findById(projectDto.getId()).orElseThrow(() -> new ProjectError("Projeto não encontrado para atualização"));


            existingProject.setName(projectDto.getName());
            existingProject.setDescription(projectDto.getDescription());
            existingProject.setTeam(team);
            projectRepository.save(existingProject);

            kafkaTemplate.send(kafkaTopic, "Novo projeto criado: " + projectDto.getName());

            response.setId(projectDto.getId());
            response.setName(projectDto.getName());
            response.setDescription(projectDto.getDescription());
            response.setTeam(team.getName());
            response.setCreatedAt(existingProject.getCreatedAt());
            return response;
        }

        User user = getAuthenticatedUser();

        Project newProject = new Project();
        newProject.setTeam(team);
        newProject.setUser(user);
        newProject.setName(projectDto.getName());
        newProject.setDescription(projectDto.getDescription());
        projectRepository.save(newProject);

        this.kafkaTemplate.send(kafkaTopic, "Novo projeto criado: " + projectDto.getName());

        response.setId(newProject.getId());
        response.setName(projectDto.getName());
        response.setDescription(projectDto.getDescription());
        response.setTeam(team.getName());
        response.setCreatedAt(newProject.getCreatedAt());
        return response;
    }


    public List<Project> getAll(int page, int size) throws ProjectError {

            User user = getAuthenticatedUser();

            kafkaTemplate.send(kafkaTopic, "Projetos recuperados com sucesso para o usuário: " + user.getId());
            return projectRepository.findByUserId(user.getId(), PageRequest.of(page, size));
    }


    public void deleteProject(Integer id) throws ProjectError {


            User user = getAuthenticatedUser();
            Project projeto = projectRepository.findById(id).orElseThrow(() -> new ProjectError("Projeto não encontrado"));

            if (!projeto.getUser().getId().equals(user.getId())) {
                throw new ProjectError("Usuario diferente do projeto");
            }
            this.kafkaTemplate.send(kafkaTopic, "projeto deletado com sucesso: ");
            projectRepository.delete(projeto);



    }


    // Atualiza um produto (sem atualizar as variações dele) usando path
    public ProjectResponse updateProject(Integer id, UpdateProjectDto updateProjectDto) throws ProjectError {


        ProjectResponse response = new ProjectResponse();

        User user = getAuthenticatedUser();

        Project project = projectRepository.findById(id).orElseThrow(()
                -> new ProjectError("Projeto não encontrado"));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new ProjectError("Usuario diferente do projeto");
        }
        if (updateProjectDto.name() != null) {
            project.setName(updateProjectDto.name());
        }
        if (updateProjectDto.description() != null) {
            project.setDescription(updateProjectDto.description());
        }
        if (updateProjectDto.team() != null) {
            Optional<Team> team = teamRepository.findByName(updateProjectDto.team());
            project.setTeam(team.orElse(null));
        }
        this.kafkaTemplate.send(kafkaTopic, "projeto atualizado com sucesso: " + project.getName());
        projectRepository.save(project);

        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setTeam(project.getTeam().getName());
        response.setCreatedAt(project.getCreatedAt());

        return response;


    }


}
