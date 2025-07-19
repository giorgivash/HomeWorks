package com.giorgi;

import com.giorgi.config.DataLoader;
import com.giorgi.config.JPAUtil;
import com.giorgi.model.Admin;
import com.giorgi.model.Customer;
import com.giorgi.service.ReservationService;
import com.giorgi.service.WorkspaceService;
import com.giorgi.ui.AdminUI;
import com.giorgi.ui.CustomerUI;
import jakarta.persistence.EntityManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            WorkspaceService workspaceService = new WorkspaceService();
            ReservationService reservationService = new ReservationService(workspaceService);

            boolean loaded = DataLoader.loadData(workspaceService, reservationService);
            if (!loaded) {
                System.out.println("Failed to load data. Application will start with no data.");
            }

            Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.name = 'AdminUser'", Admin.class)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElseGet(() -> {
                        Admin newAdmin = new Admin("AdminUser");
                        em.persist(newAdmin);
                        return newAdmin;
                    });

            Customer customer = em.createQuery("SELECT c FROM Customer c WHERE c.name = 'CustomerUser'", Customer.class)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer("CustomerUser");
                        em.persist(newCustomer);
                        return newCustomer;
                    });

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nWelcome to the Coworking Reservation System!");
                System.out.println("1. Login as Admin");
                System.out.println("2. Login as Customer");
                System.out.println("3. Exit");

                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                if (choice.equals("1")) {
                    AdminUI adminUI = new AdminUI(workspaceService, reservationService, admin);
                    adminUI.showMenu();
                } else if (choice.equals("2")) {
                    CustomerUI customerUI = new CustomerUI(workspaceService, reservationService, customer);
                    customerUI.showMenu();
                } else if (choice.equals("3")) {
                    System.out.println("Exiting the system. Goodbye!");
                    break;
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            }
            scanner.close();
        } finally {
            em.close();
            JPAUtil.close();
        }
    }
}