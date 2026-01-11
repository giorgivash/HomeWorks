package com.giorgi.service;

import com.giorgi.entity.Workspace;
import com.giorgi.model.WorkspaceModel;
import com.giorgi.repository.WorkspaceRepository;
import com.giorgi.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public void addWorkspace(WorkspaceModel workspaceModel) {
        Workspace workspace = ModelMapper.toEntity(workspaceModel);
        workspaceRepository.save(workspace);
    }

    public boolean removeWorkspaceById(Integer id) {
        if (workspaceRepository.existsById(id)) {
            workspaceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<WorkspaceModel> getAllWorkspaces() {
        return workspaceRepository.findAll().stream()
                .map(ModelMapper::toModel)
                .toList();
    }

    public Optional<WorkspaceModel> getWorkspaceById(Integer id) {
        return workspaceRepository.findById(id)
                .map(ModelMapper::toModel);
    }

    public List<WorkspaceModel> getAvailableWorkspaces() {
        return workspaceRepository.findByAvailableTrue().stream()
                .map(ModelMapper::toModel)
                .toList();
    }
}