package service;

import model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId)
                .orElseThrow(() -> new ReservationException("Workspace not found with ID: " + workspaceId));

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

    public List<Reservation> getReservationsByCustomerId(int customerId) {
        return reservationsMap.values().stream()
                .filter(r -> r.getCustomer().getID() == customerId)
                .collect(Collectors.toList());
    }

    public boolean cancelReservationById(int reservationId, int customerId) {
        return Optional.ofNullable(reservationsMap.get(reservationId))
                .filter(r -> r.getCustomer().getID() == customerId)
                .map(r -> {
                    reservationsMap.remove(reservationId);
                    reservationsByTime.remove(r.getStartTime());
                    r.getWorkspace().setAvailable(true);
                    saveReservationsToFile();
                    return true;
                })
                .orElse(false);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservationsMap.values());
    }

    private void saveReservationsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESERVATION_FILE))) {
            reservationsMap.values().forEach(r -> {
                try {
                    writer.write(
                            r.getReservationId() + "," +
                                    r.getCustomer().getID() + "," +
                                    r.getCustomer().getName() + "," +
                                    r.getWorkspace().getID() + "," +
                                    r.getStartTime() + "," +
                                    r.getEndTime()
                    );
                    writer.newLine();
                } catch (IOException e) {
                    System.out.println("Error writing reservation: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.out.println("Failed to save reservations: " + e.getMessage());
        }
    }

    public void addReservationFromFile(Reservation reservation) {
        reservationsMap.put(reservation.getReservationId(), reservation);
        reservationsByTime.put(reservation.getStartTime(), reservation);
        if (reservation.getReservationId() >= nextReservationId) {
            nextReservationId = reservation.getReservationId() + 1;
        }
    }
}