package com.giorgi.model;


import java.util.Objects;

public abstract class User {
    protected String name;
    protected int id;

    public User(String name, int id){
        this.name = name;
        this.id = id;
    }


    public void setId(int id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }

    public abstract void showMenu();


    @Override
    public String toString() {
        return    "ID = " + id + "\n"
                + "name = " + name + "\n"
                + "Role = " + this.getClass().getSimpleName();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
