package config;

import model.Workspace;
import model.Reservation;
import model.Customer;
import model.ReservationException;
import service.ReservationService;
import service.WorkspaceService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DataLoader {
    private static final String WORKSPACES_FILE = "workspaces.txt";
    private static final String RESERVATIONS_FILE = "reservations.txt";

    public static boolean loadData(WorkspaceService workspaceService, ReservationService reservationService) {
        try {
            // Load workspaces first
            if (!loadWorkspaces(workspaceService)) {
                System.out.println("Failed to load workspaces. Starting with empty data.");
                return false;
            }

            // Then load reservations
            if (!loadReservations(workspaceService, reservationService)) {
                System.out.println("Failed to load reservations. Starting with empty data.");
                return false;
            }

            return true; // All loaded successfully
        } catch (Exception e) {
            System.out.println("Critical error during data load: " + e.getMessage());
            return false;
        }
    }

    private static boolean loadWorkspaces(WorkspaceService workspaceService) {
        File file = new File(WORKSPACES_FILE);
        if (!file.exists()) return true; // No file, no problem – just start empty

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    BigDecimal price = new BigDecimal(parts[1]);
                    boolean available = Boolean.parseBoolean(parts[2]);

                    Workspace ws = new Workspace(id, price, available);
                    workspaceService.addWorkspace(ws);
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error loading workspaces: " + e.getMessage());
            return false;
        }
    }

    private static boolean loadReservations(WorkspaceService workspaceService, ReservationService reservationService) {
        File file = new File(RESERVATIONS_FILE);
        if (!file.exists()) return true; // No file, no problem – just start empty

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    int reservationId = Integer.parseInt(parts[0]);
                    int customerId = Integer.parseInt(parts[1]);
                    String customerName = parts[2];
                    int workspaceId = Integer.parseInt(parts[3]);
                    LocalDateTime start = LocalDateTime.parse(parts[4]);
                    LocalDateTime end = LocalDateTime.parse(parts[5]);

                    Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
                    if (workspace == null) {
                        throw new RuntimeException("Reservation refers to missing workspace ID: " + workspaceId);
                    }

                    Customer customer = new Customer(customerName, customerId);
                    Reservation reservation = new Reservation.Builder()
                            .setId(reservationId)
                            .setCustomer(customer)
                            .setWorkspace(workspace)
                            .setStartTime(start)
                            .setEndTime(end)
                            .build();

                    reservationService.addReservationFromFile(reservation);
                    workspace.setAvailable(false);
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            return false;
        }
    }
}
