package com.giorgi.ui;

import com.giorgi.model.WorkspaceModel;
import com.giorgi.model.ReservationModel;
import com.giorgi.service.ReservationService;
import com.giorgi.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Component
public class AdminUI {
    private final WorkspaceService workspaceService;
    private final ReservationService reservationService;
    private final Scanner scanner;

    @Autowired
    public AdminUI(WorkspaceService workspaceService,
                   ReservationService reservationService,
                   Scanner scanner) {
        this.workspaceService = workspaceService;
        this.reservationService = reservationService;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add a new coworking space");
            System.out.println("2. Remove a coworking space");
            System.out.println("3. View all reservations");
            System.out.println("4. Logout");

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
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private void addWorkspace() {
        try {
            System.out.print("Enter price per hour: ");
            BigDecimal price = new BigDecimal(scanner.nextLine());

            WorkspaceModel model = new WorkspaceModel();
            model.setPricePerHour(price);
            model.setAvailable(true);

            workspaceService.addWorkspace(model);
            System.out.println("Workspace added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid price.");
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    private void removeWorkspace() {
        try {
            System.out.print("Enter Workspace ID to remove: ");
            int id = Integer.parseInt(scanner.nextLine());

            boolean removed = workspaceService.removeWorkspaceById(id);
            System.out.println(removed ? "Workspace removed successfully."
                    : "Workspace not found.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a numeric ID.");
        }
    }

    private void viewReservations() {
        System.out.println("\nAll Reservations:");
        List<ReservationModel> reservations = reservationService.getAllReservations();

        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        reservations.forEach(res -> {
            System.out.println("ID: " + res.getId());
            System.out.println("Customer ID: " + res.getCustomerId());
            System.out.println("Workspace ID: " + res.getWorkspaceId());
            System.out.println("Start: " + res.getStartTime());
            System.out.println("End: " + res.getEndTime());
            System.out.println("--------------------");
        });
    }
}