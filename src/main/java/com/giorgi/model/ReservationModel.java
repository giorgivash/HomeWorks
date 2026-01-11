package com.giorgi.model;

import java.time.LocalDateTime;

public class ReservationModel {
    private Integer id;
    private Integer customerId;
    private Integer workspaceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ReservationModel() {}

    public ReservationModel(Integer id, Integer customerId, Integer workspaceId,
                            LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.customerId = customerId;
        this.workspaceId = workspaceId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}