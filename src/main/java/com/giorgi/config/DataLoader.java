package com.giorgi.config;


import com.giorgi.model.Workspace;
import com.giorgi.service.ReservationService;
import com.giorgi.service.WorkspaceService;

import java.math.BigDecimal;
import java.sql.*;

public class DataLoader {

    public static boolean loadData(WorkspaceService workspaceService, ReservationService reservationService) {
        return loadWorkspaces(workspaceService) && loadReservations(workspaceService, reservationService);
    }

    private static boolean loadWorkspaces(WorkspaceService workspaceService) {
        String sql = "SELECT id, price_per_hour, available FROM workspaces";
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                BigDecimal price = rs.getBigDecimal("price_per_hour");
                boolean available = rs.getBoolean("available");
                Workspace ws = new Workspace(id, price, available);
                workspaceService.addWorkspace(ws);
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error loading workspaces from DB: " + e.getMessage());
            return false;
        }
    }

    private static boolean loadReservations(WorkspaceService workspaceService, ReservationService reservationService) {
        String sql = """
            SELECT r.id, r.customer_id, c.name, r.workspace_id, r.start_time, r.end_time
            FROM reservations r
            JOIN customers c ON r.customer_id = c.id
            """;

        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int workspaceId = rs.getInt("workspace_id");

                Workspace workspace = workspaceService.getWorkspaceById(workspaceId)
                        .orElseThrow(() -> new RuntimeException("Reservation refers to missing workspace ID: " + workspaceId));

                workspace.setAvailable(false); // mark workspace as reserved
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Error loading reservations from DB: " + e.getMessage());
            return false;
        }
    }
}
