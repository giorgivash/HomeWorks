package service;

import model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReservationService {
    private final Map<Integer, Reservation> reservationsMap = new HashMap<>();
    private final TreeMap<LocalDateTime, Reservation> reservationsByTime = new TreeMap<>();
    private int nextReservationId = 1;
    private final WorkspaceService workspaceService;
    private static final String RESERVATION_FILE = "reservations.txt";

    public ReservationService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
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

        LocalDateTime start = LocalDateTime.parse(date + "T" + startTime);
        LocalDateTime end = LocalDateTime.parse(date + "T" + endTime);

        if (!end.isAfter(start)) {
            throw new ReservationException("End time must be after start time.");
        }

        if (!reservationsByTime.subMap(start, end).isEmpty()) {
            throw new ReservationException("Time slot conflicts with existing reservation");
        }

        Customer customer = new Customer(bookingName, customerId);
        Reservation reservation = new Reservation.Builder()
                .setId(nextReservationId++)
                .setCustomer(customer)
                .setWorkspace(workspace)
                .setStartTime(start)
                .setEndTime(end)
                .build();

        reservationsMap.put(reservation.getReservationId(), reservation);
        reservationsByTime.put(start, reservation);
        workspace.setAvailable(false);
        saveReservationsToFile();

        return true;
    }

    public void addReservationFromFile(Reservation reservation) {
        reservationsMap.put(reservation.getReservationId(), reservation);
        reservationsByTime.put(reservation.getStartTime(), reservation);
        if (reservation.getReservationId() >= nextReservationId) {
            nextReservationId = reservation.getReservationId() + 1;
        }
    }

    public List<Reservation> getReservationsByCustomerId(int customerId) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation reservation : reservationsMap.values()) {
            if (reservation.getCustomer().getID() == customerId) {
                result.add(reservation);
            }
        }
        return result;
    }

    public boolean cancelReservationById(int reservationId, int customerId) {
        Reservation reservation = reservationsMap.get(reservationId);
        if (reservation != null && reservation.getCustomer().getID() == customerId) {
            reservationsMap.remove(reservationId);
            reservationsByTime.remove(reservation.getStartTime());
            reservation.getWorkspace().setAvailable(true);
            saveReservationsToFile();
            return true;
        }
        return false;
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservationsMap.values());
    }

    private void saveReservationsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESERVATION_FILE))) {
            for (Reservation r : reservationsMap.values()) {
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
}