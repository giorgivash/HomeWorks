package service;

import model.Customer;
import model.Reservation;
import model.Workspace;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private final List<Reservation> reservations = new ArrayList<>();
    private int nextReservationId = 1;
    private final WorkspaceService workspaceService;

    public ReservationService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public boolean createReservation(int customerId, int workspaceId, String bookingName,
                                     String date, String startTime, String endTime) {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
        if (workspace == null || !workspace.isAvailable()) {
            return false;
        }

        String startDateTime = date + "T" + startTime;
        String endDateTime = date + "T" + endTime;

        LocalDateTime start = LocalDateTime.parse(startDateTime);
        LocalDateTime end = LocalDateTime.parse(endDateTime);

        if (end.isBefore(start) || end.equals(start)) {
            return false;
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
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }
}
