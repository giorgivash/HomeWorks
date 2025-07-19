package com.giorgi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    public Reservation() {}

    private Reservation(Builder builder) {
        this.customer = builder.customer;
        this.workspace = builder.workspace;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
    }

    public Integer getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + id + "\n" +
               "Customer: " + customer.getName() + "\n" +
               "Workspace ID: " + workspace.getId() + "\n" +
               "Start: " + startTime + "\n" +
               "End: " + endTime;
    }

    public static class Builder {
        private Customer customer;
        private Workspace workspace;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public Builder setCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder setWorkspace(Workspace workspace) {
            this.workspace = workspace;
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