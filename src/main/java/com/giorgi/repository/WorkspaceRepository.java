package com.giorgi.repository;

import com.giorgi.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, Integer> {
    List<Workspace> findByAvailableTrue(); // Replaces findAvailable()

}