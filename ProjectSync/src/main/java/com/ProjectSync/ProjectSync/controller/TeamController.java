package com.ProjectSync.ProjectSync.controller;

import com.ProjectSync.ProjectSync.dtos.TeamDto;
import com.ProjectSync.ProjectSync.entities.Team;
import com.ProjectSync.ProjectSync.exceptions.ProjectError;
import com.ProjectSync.ProjectSync.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;


    @PostMapping
    public ResponseEntity<?> createOrUpdateEquip(@RequestBody TeamDto teamDto) throws ProjectError {

        try {
            Team createdTeam = teamService.createOrUpdateEquip(teamDto);

            if (createdTeam.getName() == null) {
                throw new ProjectError("Erro na criação do projeto: nome inválidos");
            }

            HttpStatus status = (teamDto.getId() == null) ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(createdTeam);
        }catch (Exception e) {
            throw new ProjectError(e.getMessage());
        }

    }

}
