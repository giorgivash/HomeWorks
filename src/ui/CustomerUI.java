package ui;

import model.Customer;
import model.Reservation;
import model.ReservationException;
import model.Workspace;
import service.ReservationService;
import service.WorkspaceService;

import java.util.List;
import java.util.Scanner;

public class CustomerUI {
    private final WorkspaceService workspaceService;
    private final ReservationService reservationService;
    private final Customer customer;
    private final Scanner scanner;

    public CustomerUI(WorkspaceService workspaceService, ReservationService reservationService, Customer customer) {
        this.workspaceService = workspaceService;
        this.reservationService = reservationService;
        this.customer = customer;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. Browse available spaces");
            System.out.println("2. Make a reservation");
            System.out.println("3. View my reservations");
            System.out.println("4. Cancel a reservation");
            System.out.println("5. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                browseAvailableSpaces();
            } else if (choice.equals("2")) {
                makeReservation();
            } else if (choice.equals("3")) {
                viewMyReservations();
            } else if (choice.equals("4")) {
                cancelReservation();
            } else if (choice.equals("5")) {
                System.out.println("Logging out...");
                break;
            } else {
                System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private void browseAvailableSpaces() {
        System.out.println("\nAvailable Workspaces:");
        List<Workspace> availableSpaces = workspaceService.getAvailableWorkspaces();
        if (availableSpaces.isEmpty()) {
            System.out.println("No available workspaces right now.");
        } else {
            for (Workspace ws : availableSpaces) {
                System.out.println(ws);
                System.out.println("--------------------");
            }
        }
    }

    private void makeReservation() {
        try {
            System.out.print("Enter Workspace ID to reserve: ");
            int wsId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter booking name: ");
            String bookingName = scanner.nextLine();

            System.out.print("Enter booking date (YYYY-MM-DD): ");
            String date = scanner.nextLine();

            System.out.print("Enter start time (HH:mm): ");
            String startTime = scanner.nextLine();

            System.out.print("Enter end time (HH:mm): ");
            String endTime = scanner.nextLine();

            boolean success = reservationService.createReservation(
                    customer.getID(), wsId, bookingName, date, startTime, endTime
            );

            if (success) {
                System.out.println("Reservation created successfully.");
            } else {
                System.out.println("Failed to create reservation. Check if workspace is available.");
            }

        } catch (ReservationException e) {
            System.out.println("Reservation failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: Invalid input or time format.");
        }
    }

    private void viewMyReservations() {
        System.out.println("\nYour Reservations:");
        List<Reservation> myReservations = reservationService.getReservationsByCustomerId(customer.getID());
        if (myReservations.isEmpty()) {
            System.out.println("You have no reservations.");
        } else {
            for (Reservation res : myReservations) {
                System.out.println(res);
                System.out.println("--------------------");
            }
        }
    }

    private void cancelReservation() {
        System.out.print("Enter Reservation ID to cancel: ");
        int resId = Integer.parseInt(scanner.nextLine());

        boolean canceled = reservationService.cancelReservationById(resId, customer.getID());

        if (canceled) {
            System.out.println("Reservation canceled successfully.");
        } else {
            System.out.println("Reservation not found or you do not have permission to cancel it.");
        }
    }
}
