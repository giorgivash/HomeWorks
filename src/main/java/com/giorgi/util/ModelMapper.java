package com.giorgi.util;

import com.giorgi.entity.Reservation;
import com.giorgi.entity.Workspace;
import com.giorgi.model.ReservationModel;
import com.giorgi.model.WorkspaceModel;

public class ModelMapper {
    public static Workspace toEntity(WorkspaceModel model) {
        Workspace entity = new Workspace();
        entity.setId(model.getId());
        entity.setPricePerHour(model.getPricePerHour());
        entity.setAvailable(model.isAvailable());
        return entity;
    }

    public static WorkspaceModel toModel(Workspace entity) {
        return new WorkspaceModel(
                entity.getId(),
                entity.getPricePerHour(),
                entity.isAvailable()
        );
    }

    public static Reservation toEntity(ReservationModel model) {
        return new Reservation.Builder()
                .setStartTime(model.getStartTime())
                .setEndTime(model.getEndTime())
                .build();
    }

    public static ReservationModel toModel(Reservation entity) {
        return new ReservationModel(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getWorkspace().getId(),
                entity.getStartTime(),
                entity.getEndTime()
        );
    }
}