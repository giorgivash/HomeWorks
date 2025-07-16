package com.giorgi.service;

import com.giorgi.config.DBConnector;
import com.giorgi.model.Customer;
import com.giorgi.model.Reservation;
import com.giorgi.model.Workspace;
import com.giorgi.model.ReservationException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReservationService {
    private final WorkspaceService workspaceService;

    public ReservationService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public boolean createReservation(int customerId, int workspaceId, String bookingName,
                                     String date, String startTime, String endTime) throws ReservationException {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId)
                .orElseThrow(() -> new ReservationException("Workspace not found with ID: " + workspaceId));

        if (!workspace.isAvailable()) {
            throw new ReservationException("Workspace is not available.");
        }

        LocalDateTime start = LocalDateTime.parse(date + "T" + startTime);
        LocalDateTime end = LocalDateTime.parse(date + "T" + endTime);

        if (!end.isAfter(start)) {
            throw new ReservationException("End time must be after start time.");
        }

        if (reservationExistsInDB(workspaceId, start, end)) {
            throw new ReservationException("Time slot conflicts with existing reservation.");
        }

        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);

            // insert customer if not exists
            String customerInsert = "INSERT INTO customers (id, name) VALUES (?, ?) ON CONFLICT (id) DO NOTHING";
            try (PreparedStatement ps = conn.prepareStatement(customerInsert)) {
                ps.setInt(1, customerId);
                ps.setString(2, bookingName);
                ps.executeUpdate();
            }

            // insert reservation
            String insertSQL = """
                    INSERT INTO reservations (id, customer_id, workspace_id, start_time, end_time)
                    VALUES (DEFAULT, ?, ?, ?, ?) RETURNING id
                    """;

            int reservationId;
            try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
                ps.setInt(1, customerId);
                ps.setInt(2, workspaceId);
                ps.setTimestamp(3, Timestamp.valueOf(start));
                ps.setTimestamp(4, Timestamp.valueOf(end));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    reservationId = rs.getInt("id");
                } else {
                    conn.rollback();
                    throw new ReservationException("Failed to create reservation.");
                }
            }

            // update workspace availability
            String updateWorkspace = "UPDATE workspaces SET available = false WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateWorkspace)) {
                ps.setInt(1, workspaceId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            throw new ReservationException("DB error: " + e.getMessage());
        }
    }

    public List<Reservation> getReservationsByCustomerId(int customerId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = """
                SELECT r.id, r.workspace_id, r.start_time, r.end_time, c.name
                FROM reservations r
                JOIN customers c ON r.customer_id = c.id
                WHERE c.id = ?
                """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservations.add(mapRowToReservation(rs, customerId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public boolean cancelReservationById(int reservationId, int customerId) {
        String selectSQL = "SELECT workspace_id FROM reservations WHERE id = ? AND customer_id = ?";
        String deleteSQL = "DELETE FROM reservations WHERE id = ?";
        String updateWorkspace = "UPDATE workspaces SET available = true WHERE id = ?";

        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);

            int workspaceId = -1;
            try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
                ps.setInt(1, reservationId);
                ps.setInt(2, customerId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    workspaceId = rs.getInt("workspace_id");
                } else {
                    return false;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
                ps.setInt(1, reservationId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateWorkspace)) {
                ps.setInt(1, workspaceId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = """
                SELECT r.id, r.customer_id, c.name, r.workspace_id, r.start_time, r.end_time
                FROM reservations r
                JOIN customers c ON r.customer_id = c.id
                """;

        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int customerId = rs.getInt("customer_id");
                reservations.add(mapRowToReservation(rs, customerId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    private Reservation mapRowToReservation(ResultSet rs, int customerId) throws SQLException {
        int reservationId = rs.getInt("id");
        String customerName = rs.getString("name");
        int workspaceId = rs.getInt("workspace_id");
        LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
        LocalDateTime end = rs.getTimestamp("end_time").toLocalDateTime();

        Workspace workspace = workspaceService.getWorkspaceById(workspaceId)
                .orElse(new Workspace(workspaceId, null, false));
        Customer customer = new Customer(customerName, customerId);

        return new Reservation.Builder()
                .setId(reservationId)
                .setCustomer(customer)
                .setWorkspace(workspace)
                .setStartTime(start)
                .setEndTime(end)
                .build();
    }

    private boolean reservationExistsInDB(int workspaceId, LocalDateTime start, LocalDateTime end) {
        String query = """
                SELECT 1 FROM reservations
                WHERE workspace_id = ?
                AND (start_time, end_time) OVERLAPS (?, ?)
                """;
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, workspaceId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
}
