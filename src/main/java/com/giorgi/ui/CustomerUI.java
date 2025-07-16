package com.giorgi.ui;

import com.giorgi.loader.DynamicClassLoader;
import com.giorgi.model.*;
import com.giorgi.service.*;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomerUI {
    private final WorkspaceService workspaceService;
    private final ReservationService reservationService;
    private final Customer customer;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter TIME_INPUT_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    public CustomerUI(WorkspaceService workspaceService, ReservationService reservationService, Customer customer) {
        this.workspaceService = workspaceService;
        this.reservationService = reservationService;
        this.customer = customer;
        this.scanner = new Scanner(System.in);
    }

    private void browseAvailableSpaces() {
        System.out.println("\nAvailable Workspaces:");
        List<Workspace> availableWorkspaces = workspaceService.getAvailableWorkspaces();

        if (availableWorkspaces.isEmpty()) {
            System.out.println("No available workspaces right now.");
            return;
        }

        availableWorkspaces.forEach(ws -> {
            System.out.println("ID: " + ws.getId());
            System.out.println("Price per hour: $" + ws.getPricePerHour());
            System.out.println("--------------------");
        });
    }

    private void viewMyReservations() {
        System.out.println("\nYour Reservations:");
        List<Reservation> reservations = reservationService.getReservationsByCustomerId(customer.getId());

        if (reservations.isEmpty()) {
            System.out.println("You have no reservations.");
            return;
        }

        reservations.forEach(res -> {
            System.out.println("Reservation ID: " + res.getId());
            System.out.println("Workspace ID: " + res.getWorkspace().getId());
            System.out.println("Start Time: " + res.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            System.out.println("End Time: " + res.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            System.out.println("--------------------");
        });
    }

    public void showMenu() {
        showGreeting();
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
            System.out.print("Enter Workspace ID to reserve: ");
            int wsId = Integer.parseInt(scanner.nextLine());

            Optional<Workspace> workspaceOpt = workspaceService.getWorkspaceById(wsId);
            if (workspaceOpt.isEmpty()) {
                System.out.println("Workspace not found!");
                return;
            }

            System.out.print("Enter booking date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine();
            System.out.print("Enter start time (HH:mm): ");
            String startTimeStr = formatTimeInput(scanner.nextLine());
            System.out.print("Enter end time (HH:mm): ");
            String endTimeStr = formatTimeInput(scanner.nextLine());

            LocalDateTime start = LocalDateTime.parse(
                    dateStr + "T" + startTimeStr,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            );
            LocalDateTime end = LocalDateTime.parse(
                    dateStr + "T" + endTimeStr,
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
            );

            boolean success = reservationService.createReservation(
                    customer,
                    workspaceOpt.get(),
                    start,
                    end
            );

            System.out.println(success ? "Reservation created successfully." : "Failed to create reservation.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String formatTimeInput(String timeInput) {
        try {
            String[] parts = timeInput.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            return String.format("%02d:%02d", hours, minutes);
        } catch (Exception e) {
            System.out.println("Invalid time format. Using default 00:00");
            return "00:00";
        }
    }

    private void cancelReservation() {
        try {
            System.out.print("Enter Reservation ID to cancel: ");
            int resId = Integer.parseInt(scanner.nextLine());

            boolean canceled = reservationService.cancelReservationById(resId);
            System.out.println(canceled ? "Reservation canceled successfully." : "Failed to cancel reservation.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid reservation ID format!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showGreeting() {
        try {
            String dynamicClassesPath = "C:/Users/Giorgi/Desktop/java/HomeWorks";
            DynamicClassLoader loader = new DynamicClassLoader(dynamicClassesPath);

            Class<?> greetingClass = loader.loadClass("dynamic_classes.Greeting");
            Object instance = greetingClass.getDeclaredConstructor().newInstance();
            Method greetMethod = greetingClass.getMethod("greet", String.class);
            greetMethod.invoke(instance, customer.getName());
        } catch (Exception e) {
            System.out.println("Welcome, " + customer.getName() + "! We're glad to have you here.");
        }
    }
}