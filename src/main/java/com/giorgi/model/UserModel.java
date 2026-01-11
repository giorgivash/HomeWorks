package com.giorgi.model;

public class UserModel {
    private Integer id;
    private String name;
    private String role; // "ADMIN" or "CUSTOMER"

    public UserModel() {}

    public UserModel(Integer id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) {
        if(role.equals("ADMIN") || role.equals("CUSTOMER")) {
            this.role = role;
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
    }
}