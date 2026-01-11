package com.giorgi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "workspaces")
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "price_per_hour", nullable = false, precision = 10, scale = 2)
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerHour;

    @Column(nullable = false)
    private boolean available;

    public Workspace() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Workspace ID: " + id + "\n" +
                "Price per hour: $" + pricePerHour + "\n" +
                "Available: " + (available ? "Yes" : "No");
    }
}