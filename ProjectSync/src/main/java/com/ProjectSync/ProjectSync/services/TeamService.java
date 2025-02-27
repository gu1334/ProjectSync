package com.ProjectSync.ProjectSync.services;

import com.ProjectSync.ProjectSync.dtos.TeamDto;
import com.ProjectSync.ProjectSync.entities.Team;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public Team createOrUpdateEquip(TeamDto teamDto) throws ProjectError {
        try {

            Team teamjaexiste = teamRepository.findByName(teamDto.getName());
            if (teamjaexiste != null) {
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
                return teamRepository.save(existingProject);
            }
            // Criação de novo projeto

            Team newTeam = new Team();
            newTeam.setName(teamDto.getName());
            return teamRepository.save(newTeam);
        } catch (Exception e) {
            throw new ProjectError(e.getMessage());
        }


    }

}
