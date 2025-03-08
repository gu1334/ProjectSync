package com.ProjectSync.ProjectSync.dtos;

import com.ProjectSync.ProjectSync.entities.Team;

public record UpdateProjectDto(
        String name,
        String description,
        String team
) {


}
