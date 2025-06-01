package ui;

import model.Admin;
import model.Workspace;
import service.ReservationService;
import service.WorkspaceService;

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
        System.out.print("Enter Workspace ID (integer): ");
        String idStr = scanner.nextLine();
        int id = Integer.parseInt(idStr);

        System.out.print("Enter price per hour: ");
        String priceStr = scanner.nextLine();
        double price = Double.parseDouble(priceStr);

        Workspace ws = new Workspace(id, price, true);
        workspaceService.addWorkspace(ws);
        System.out.println("Workspace added successfully.");
    }

    private void removeWorkspace() {
        System.out.print("Enter Workspace ID to remove: ");
        String idStr = scanner.nextLine();
        int id = Integer.parseInt(idStr);

        boolean removed = workspaceService.removeWorkspaceById(id);
        if (removed) {
            System.out.println("Workspace removed.");
        } else {
            System.out.println("Workspace with given ID not found.");
        }
    }

    private void viewReservations() {
        System.out.println("\nAll Reservations:");
        List<model.Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            for (model.Reservation res : reservations) {
                System.out.println(res);
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$");
            }
        }
    }
}
