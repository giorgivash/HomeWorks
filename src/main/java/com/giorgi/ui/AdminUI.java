package com.giorgi.ui;

import com.giorgi.model.*;
import com.giorgi.service.ReservationService;
import com.giorgi.service.WorkspaceService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class AdminUI {
    private final WorkspaceService workspaceService;
    private final ReservationService reservationService;
    private final Admin admin;
    private final Scanner scanner;

    public AdminUI(WorkspaceService workspaceService, ReservationService reservationService, Admin admin) {
        this.workspaceService = workspaceService;
        this.reservationService = reservationService;
        this.admin = admin;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        label:
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add a new coworking space");
            System.out.println("2. Remove a coworking space");
            System.out.println("3. View all reservations");
            System.out.println("4. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addWorkspace();
                    break;
                case "2":
                    removeWorkspace();
                    break;
                case "3":
                    viewReservations();
                    break;
                case "4":
                    System.out.println("Logging out...");
                    break label;
                default:
                    System.out.println("Invalid choice! Try again.");
                    break;
            }
        }
    }

    private void addWorkspace() {
        try {
            System.out.print("Enter price per hour: ");
            BigDecimal price = new BigDecimal(scanner.nextLine());

            Workspace ws = new Workspace();
            ws.setPricePerHour(price);
            ws.setAvailable(true);

            workspaceService.addWorkspace(ws);
            System.out.println("Workspace added successfully with ID: " + ws.getId());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid price.");
        } catch (Exception e) {
            System.out.println("Error occurred while adding workspace: " + e.getMessage());
        }
    }

    private void removeWorkspace() {
        try {
            System.out.print("Enter Workspace ID to remove: ");
            int id = Integer.parseInt(scanner.nextLine());

            boolean removed = workspaceService.removeWorkspaceById(id);
            if (removed) {
                System.out.println("Workspace removed successfully.");
            } else {
                System.out.println("Workspace with given ID not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a numeric ID.");
        } catch (Exception e) {
            System.out.println("Error occurred while removing workspace: " + e.getMessage());
        }
    }

    private void viewReservations() {
        System.out.println("\nAll Reservations:");
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            for (Reservation res : reservations) {
                System.out.println("ID: " + res.getId());
                System.out.println("Customer: " + res.getCustomer().getName());
                System.out.println("Workspace ID: " + res.getWorkspace().getId());
                System.out.println("Start: " + res.getStartTime());
                System.out.println("End: " + res.getEndTime());
                System.out.println("--------------------");
            }
        }
    }
}