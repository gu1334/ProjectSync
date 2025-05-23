package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.dtos.TeamDto;
import com.ProjectSync.ProjectSync.dtos.UpdateTeamDto;
import com.ProjectSync.ProjectSync.entities.Team;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    @Value("${spring.kafka.topic.bora-praticar}")
    private String kafka;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private TeamRepository teamRepository;

    public Team createOrUpdateEquip(TeamDto teamDto) throws ProjectError {
        try {

            Optional<Team> teamjaexiste = teamRepository.findByName(teamDto.getName());
            if (teamjaexiste.isPresent()) {
                throw new ProjectError("Team with this name already exists");
            }

            if (teamDto.getName() == null) {
                throw new ProjectError("Nome é obrigatório");
            }
            // Atualização de projeto
            if (teamDto.getId() != null) {
                Team existingProject = teamRepository.findById(teamDto.getId())
                        .orElseThrow(() -> new ProjectError("Projeto não encontrado para atualização"));

                existingProject.setName(teamDto.getName());
                this.kafkaTemplate.send(kafka, "projeto atualizado com sucesso: " + teamDto.getName());
                return teamRepository.save(existingProject);
            }
            // Criação de novo projeto

            Team newTeam = new Team();
            newTeam.setName(teamDto.getName());
            this.kafkaTemplate.send(kafka, "projeto criado com sucesso: " + newTeam.getName());
            return teamRepository.save(newTeam);
        } catch (Exception e) {
            throw new ProjectError(e.getMessage());
        }


    }

    // Atualização feita com path
    public Team upadateTeam(Integer id, UpdateTeamDto updateTeamDto) throws ProjectError {

        try {

            Team team = teamRepository.findById(id)
                    .orElseThrow(() -> new ProjectError("Projeto não encontrado"));
            if (updateTeamDto.name() != null) {
                team.setName(updateTeamDto.name());
            }
            this.kafkaTemplate.send(kafka, "projeto atualizado com sucesso: " + team.getName());

            return teamRepository.save(team);

        } catch (Exception e) {
            throw new ProjectError("Erro para atualizar o nome do time: " + e.getMessage());
        }

    }

    public List<Team> getAll() throws ProjectError {

        try {
            return teamRepository.findAll();

        } catch (Exception e) {
            throw new ProjectError("Erro Listar os times e suas tarefas: " + e.getMessage());
        }

    }
}
