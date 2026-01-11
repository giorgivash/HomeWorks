package com.giorgi.model;

import java.math.BigDecimal;

public class WorkspaceModel {
    private Integer id;
    private BigDecimal pricePerHour;
    private boolean available;

    public WorkspaceModel() {}

    public WorkspaceModel(Integer id, BigDecimal pricePerHour, boolean available) {
        this.id = id;
        this.pricePerHour = pricePerHour;
        this.available = available;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public BigDecimal getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "WorkspaceModel[id=" + id +
                ", price=" + pricePerHour +
                ", available=" + available + "]";
    }
}