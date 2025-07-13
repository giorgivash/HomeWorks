package com.giorgi.model;

import java.math.BigDecimal;

public class Workspace {
    private int id;
    private boolean isAvailable;
    private BigDecimal pricePerHour;

    public Workspace(int ID, BigDecimal pricePerHour, boolean isAvailable) {
        this.id = ID;
        this.pricePerHour = pricePerHour;
        this.isAvailable = isAvailable;
    }


    public void setId(int id){
        this.id = id;
    }
    public void setPricePerHour(BigDecimal pricePerHour){
        this.pricePerHour = pricePerHour;
    }
    public void setAvailable(boolean isAvailable){
        this.isAvailable = isAvailable;
    }


    public int getId(){
        return id;
    }
    public BigDecimal getPricePerHour(){
        return pricePerHour;
    }
    public boolean isAvailable(){
        return isAvailable;
    }


    @Override
    public String toString() {
        return  "Workspace ID: " + id + "\n" +
                "Price per hour: $" + pricePerHour + "\n" +
                "Available: " + (isAvailable ? "Yes" : "No");
    }

}
