package model;

public class Workspace {
    private int ID;
    private double pricePerHour;
    private boolean isAvailable;

    public Workspace(int ID, double pricePerHour, boolean isAvailable){
        this.ID = ID;
        this.pricePerHour = pricePerHour;
        this.isAvailable = isAvailable;
    }


    public void setID(int ID){
        this.ID = ID;
    }
    public void setPricePerHour(double pricePerHour){
        this.pricePerHour = pricePerHour;
    }
    public void setAvailable(boolean isAvailable){
        this.isAvailable = isAvailable;
    }


    public int getID(){
        return ID;
    }
    public double getPricePerHour(){
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
