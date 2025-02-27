package com.ProjectSync.ProjectSync.repositories;

import com.ProjectSync.ProjectSync.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    Team findByName(String name);

}
