package model;

import java.math.BigDecimal;

public class Workspace {
    private int ID;
    private boolean isAvailable;
    private BigDecimal pricePerHour;

    public Workspace(int ID, BigDecimal pricePerHour, boolean isAvailable) {
        this.ID = ID;
        this.pricePerHour = pricePerHour;
        this.isAvailable = isAvailable;
    }


    public void setID(int ID){
        this.ID = ID;
    }
    public void setPricePerHour(BigDecimal pricePerHour){
        this.pricePerHour = pricePerHour;
    }
    public void setAvailable(boolean isAvailable){
        this.isAvailable = isAvailable;
    }


    public int getID(){
        return ID;
    }
    public BigDecimal getPricePerHour(){
        return pricePerHour;
    }
    public boolean isAvailable(){
        return isAvailable;
    }


    @Override
    public String toString() {
        return  "Workspace ID: " + ID + "\n" +
                "Price per hour: $" + pricePerHour + "\n" +
                "Available: " + (isAvailable ? "Yes" : "No");
    }

}
