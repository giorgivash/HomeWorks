package com.giorgi.model;

import java.time.LocalDateTime;

public class Reservation {
    private final int reservationId;
    private final Workspace workspace;
    private final Customer customer;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;


    private Reservation(Builder builder) {
        this.reservationId = builder.reservationId;
        this.workspace = builder.workspace;
        this.customer = builder.customer;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
    }

    public int getReservationId() {
        return reservationId;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }


    @Override
    public String toString() {
        return "Reservation ID: " + reservationId + "\n" +
                "Customer: " + customer.getName() + "\n" +
                "Workspace ID: " + workspace.getId() + "\n" +
                "Start: " + startTime + "\n" +
                "End: " + endTime;
    }


    public static class Builder {
        private int reservationId;
        private Workspace workspace;
        private Customer customer;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public Builder setId(int reservationId) {
            this.reservationId = reservationId;
            return this;
        }

        public Builder setWorkspace(Workspace workspace) {
            this.workspace = workspace;
            return this;
        }

        public Builder setCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Reservation build() {
            return new Reservation(this);
        }
    }
}