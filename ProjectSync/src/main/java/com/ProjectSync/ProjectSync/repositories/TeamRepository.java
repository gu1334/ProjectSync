package com.ProjectSync.ProjectSync.repositories;

import com.ProjectSync.ProjectSync.entities.Team;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Integer> {

   Optional <Team> findByName(String name);



}
