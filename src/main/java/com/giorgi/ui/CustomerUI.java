package com.giorgi.ui;

import com.giorgi.model.ReservationModel;
import com.giorgi.model.WorkspaceModel;
import com.giorgi.service.ReservationService;
import com.giorgi.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Component
public class CustomerUI {
    private final WorkspaceService workspaceService;
    private final ReservationService reservationService;
    private final Scanner scanner;
    private Integer customerId;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public CustomerUI(WorkspaceService workspaceService,
                      ReservationService reservationService,
                      Scanner scanner) {
        this.workspaceService = workspaceService;
        this.reservationService = reservationService;
        this.scanner = scanner;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    private void browseAvailableSpaces() {
        System.out.println("\nAvailable Workspaces:");
        List<WorkspaceModel> workspaces = workspaceService.getAvailableWorkspaces();

        if (workspaces == null || workspaces.isEmpty()) {
            System.out.println("No available workspaces right now.");
            return;
        }

        for (WorkspaceModel ws : workspaces) {
            System.out.println("ID: " + ws.getId());
            System.out.println("Price per hour: $" + ws.getPricePerHour());
            System.out.println("--------------------");
        }
    }

    private void viewMyReservations() {
        System.out.println("\nYour Reservations:");
        List<ReservationModel> reservations = reservationService.getReservationsByCustomerId(customerId);

        if (reservations == null || reservations.isEmpty()) {
            System.out.println("You have no reservations.");
            return;
        }

        for (ReservationModel res : reservations) {
            System.out.println("Reservation ID: " + res.getId());
            System.out.println("Workspace ID: " + res.getWorkspaceId());
            System.out.println("Start Time: " + res.getStartTime().format(DISPLAY_FORMAT));
            System.out.println("End Time: " + res.getEndTime().format(DISPLAY_FORMAT));
            System.out.println("--------------------");
        }
    }

    public void showMenu() {
        while (true) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. Browse available spaces");
            System.out.println("2. Make a reservation");
            System.out.println("3. View my reservations");
            System.out.println("4. Cancel a reservation");
            System.out.println("5. Logout");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    browseAvailableSpaces();
                    break;
                case "2":
                    makeReservation();
                    break;
                case "3":
                    viewMyReservations();
                    break;
                case "4":
                    cancelReservation();
                    break;
                case "5":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private void makeReservation() {
        try {
            System.out.print("Enter Workspace ID: ");
            int workspaceId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = scanner.nextLine();

            System.out.print("Enter start time (HH:mm): ");
            String startTime = scanner.nextLine();

            System.out.print("Enter end time (HH:mm): ");
            String endTime = scanner.nextLine();

            ReservationModel model = new ReservationModel();
            model.setCustomerId(customerId);
            model.setWorkspaceId(workspaceId);
            model.setStartTime(LocalDateTime.parse(date + "T" + startTime));
            model.setEndTime(LocalDateTime.parse(date + "T" + endTime));

            boolean success = reservationService.createReservation(model);
            System.out.println(success ? "Reservation created successfully!"
                    : "Failed to create reservation.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cancelReservation() {
        try {
            System.out.print("Enter Reservation ID to cancel: ");
            int id = Integer.parseInt(scanner.nextLine());

            if (reservationService.cancelReservationById(id)) {
                System.out.println("Reservation canceled.");
            } else {
                System.out.println("Failed to cancel reservation.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format!");
        }
    }
}