package ui;

import loader.DynamicClassLoader;
import model.*;
import service.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

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

    private void browseAvailableSpaces() {
        System.out.println("\nAvailable Workspaces:");
        workspaceService.getAvailableWorkspaces().stream()
                .peek(System.out::println)
                .collect(Collectors.toList())
                .forEach(ws -> System.out.println("--------------------"));

        if (workspaceService.getAvailableWorkspaces().isEmpty()) {
            System.out.println("No available workspaces right now.");
        }
    }

    private void viewMyReservations() {
        System.out.println("\nYour Reservations:");
        reservationService.getReservationsByCustomerId(customer.getID()).stream()
                .peek(System.out::println)
                .collect(Collectors.toList())
                .forEach(res -> System.out.println("--------------------"));

        if (reservationService.getReservationsByCustomerId(customer.getID()).isEmpty()) {
            System.out.println("You have no reservations.");
        }
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

            if (choice.equals("1")) browseAvailableSpaces();
            else if (choice.equals("2")) makeReservation();
            else if (choice.equals("3")) viewMyReservations();
            else if (choice.equals("4")) cancelReservation();
            else if (choice.equals("5")) {
                System.out.println("Logging out...");
                break;
            } else {
                System.out.println("Invalid choice! Try again.");
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
                    customer.getID(), wsId, bookingName, date, startTime, endTime);

            System.out.println(success ? "Reservation created successfully." : "Failed to create reservation.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cancelReservation() {
        System.out.print("Enter Reservation ID to cancel: ");
        int resId = Integer.parseInt(scanner.nextLine());
        boolean canceled = reservationService.cancelReservationById(resId, customer.getID());
        System.out.println(canceled ? "Reservation canceled successfully." : "Failed to cancel reservation.");
    }

    private void showGreeting() {
        try {
            DynamicClassLoader loader = new DynamicClassLoader("dynamic_classes");
            Class<?> greetingClass = loader.loadClass("dynamic_classes.Greeting");
            Object instance = greetingClass.getDeclaredConstructor().newInstance();
            Method greetMethod = greetingClass.getMethod("greet", String.class);
            greetMethod.invoke(instance, customer.getName());
        } catch (Exception e) {
            System.out.println("Greeting unavailable: " + e.getMessage());
        }
    }
}