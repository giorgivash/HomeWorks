package model;


import java.util.Objects;

public abstract class User {
    protected String name;
    protected int ID;

    public User(String name, int ID){
        this.name = name;
        this.ID = ID;
    }


    public void setID(int ID){
        this.ID = ID;
    }
    public void setName(String name){
        this.name = name;
    }

    public int getID(){
        return ID;
    }
    public String getName(){
        return name;
    }

    public abstract void showMenu();


    @Override
    public String toString() {
        return    "ID = " + ID + "\n"
                + "name = " + name + "\n"
                + "Role = " + this.getClass().getSimpleName();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return ID == user.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }


}
