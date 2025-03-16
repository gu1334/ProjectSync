package com.ProjectSync.ProjectSync.repositories;

import com.ProjectSync.ProjectSync.entities.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, Integer> {


    List<Project> findByUserId(UUID uuid, Pageable pageable);


}
