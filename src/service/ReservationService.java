package service;

import model.Customer;
import model.Reservation;
import model.ReservationException;
import model.Workspace;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private final List<Reservation> reservations = new ArrayList<>();
    private int nextReservationId = 1;
    private final WorkspaceService workspaceService;

    private static final String RESERVATION_FILE = "reservations.txt";

    public ReservationService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
        loadReservationsFromFile();
    }

    public boolean createReservation(int customerId, int workspaceId, String bookingName,
                                     String date, String startTime, String endTime) throws ReservationException {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
        if (workspace == null) {
            throw new ReservationException("Workspace not found with ID: " + workspaceId);
        }
        if (!workspace.isAvailable()) {
            throw new ReservationException("Workspace is currently not available.");
        }

        String startDateTime = date + "T" + startTime;
        String endDateTime = date + "T" + endTime;

        LocalDateTime start;
        LocalDateTime end;
        try {
            start = LocalDateTime.parse(startDateTime);
            end = LocalDateTime.parse(endDateTime);
        } catch (Exception e) {
            throw new ReservationException("Invalid date or time format.");
        }

        if (!end.isAfter(start)) {
            throw new ReservationException("End time must be after start time.");
        }

        Customer customer = new Customer(bookingName, customerId);

        Reservation reservation = new Reservation.Builder()
                .setId(nextReservationId++)
                .setCustomer(customer)
                .setWorkspace(workspace)
                .setStartTime(start)
                .setEndTime(end)
                .build();

        reservations.add(reservation);
        workspace.setAvailable(false);

        saveReservationsToFile();

        return true;
    }

    public List<Reservation> getReservationsByCustomerId(int customerId) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getCustomer().getID() == customerId) {
                result.add(reservation);
            }
        }
        return result;
    }

    public boolean cancelReservationById(int reservationId, int customerId) {
        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            if (reservation.getReservationId() == reservationId &&
                    reservation.getCustomer().getID() == customerId) {
                reservation.getWorkspace().setAvailable(true);
                reservations.remove(i);

                saveReservationsToFile();

                return true;
            }
        }
        return false;
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    private void saveReservationsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESERVATION_FILE))) {
            for (Reservation r : reservations) {
                writer.write(
                        r.getReservationId() + "," +
                                r.getCustomer().getID() + "," +
                                r.getCustomer().getName() + "," +
                                r.getWorkspace().getID() + "," +
                                r.getStartTime() + "," +
                                r.getEndTime()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Failed to save reservations: " + e.getMessage());
        }
    }

    private void loadReservationsFromFile() {
        File file = new File(RESERVATION_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length == 6) {
                    int reservationId = Integer.parseInt(parts[0]);
                    int customerId = Integer.parseInt(parts[1]);
                    String customerName = parts[2];
                    int workspaceId = Integer.parseInt(parts[3]);
                    LocalDateTime start = LocalDateTime.parse(parts[4]);
                    LocalDateTime end = LocalDateTime.parse(parts[5]);

                    Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
                    if (workspace != null) {
                        workspace.setAvailable(false);

                        Customer customer = new Customer(customerName, customerId);
                        Reservation reservation = new Reservation.Builder()
                                .setId(reservationId)
                                .setCustomer(customer)
                                .setWorkspace(workspace)
                                .setStartTime(start)
                                .setEndTime(end)
                                .build();

                        reservations.add(reservation);

                        if (reservationId >= nextReservationId) {
                            nextReservationId = reservationId + 1;
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Failed to load reservations: " + e.getMessage());
        }
    }
}
