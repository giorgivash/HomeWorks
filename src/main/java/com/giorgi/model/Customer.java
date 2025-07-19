package com.giorgi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends User {
    public Customer() {}

    public Customer(String name) {
        super(name);
    }

    @Override
    public void showMenu() {
        System.out.println("\n--- Customer Menu ---");
        System.out.println("1. Browse available spaces");
        System.out.println("2. Make a reservation");
        System.out.println("3. View my reservations");
        System.out.println("4. Cancel a reservation");
        System.out.println("5. Exit to main menu");
    }
}