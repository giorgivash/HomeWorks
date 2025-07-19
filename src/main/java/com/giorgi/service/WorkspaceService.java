package com.giorgi.service;

import com.giorgi.config.DBConnector;
import com.giorgi.model.Workspace;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorkspaceService {
    public void addWorkspace(Workspace workspace) {
        // First check if workspace exists
        if (getWorkspaceById(workspace.getId()).isPresent()) {
            System.out.println("Workspace with id " + workspace.getId() + " already exists");
            return;
        }

        String sql = "INSERT INTO workspaces (id, price_per_hour, available) VALUES (?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, workspace.getId());
            stmt.setBigDecimal(2, workspace.getPricePerHour());
            stmt.setBoolean(3, workspace.isAvailable());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding workspace: " + e.getMessage());
        }
    }

    public boolean removeWorkspaceById(int id) {
        String sql = "DELETE FROM workspaces WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error removing workspace: " + e.getMessage());
            return false;
        }
    }

    public List<Workspace> getAllWorkspaces() {
        List<Workspace> workspaces = new ArrayList<>();
        String sql = "SELECT * FROM workspaces";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Workspace ws = new Workspace(
                        rs.getInt("id"),
                        rs.getBigDecimal("price_per_hour"),
                        rs.getBoolean("available")
                );
                workspaces.add(ws);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all workspaces: " + e.getMessage());
        }

        return workspaces;
    }

    public List<Workspace> getAvailableWorkspaces() {
        List<Workspace> available = new ArrayList<>();
        String sql = "SELECT * FROM workspaces WHERE available = true";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Workspace ws = new Workspace(
                        rs.getInt("id"),
                        rs.getBigDecimal("price_per_hour"),
                        rs.getBoolean("available")
                );
                available.add(ws);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching available workspaces: " + e.getMessage());
        }

        return available;
    }

    public Optional<Workspace> getWorkspaceById(int id) {
        String sql = "SELECT * FROM workspaces WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Workspace ws = new Workspace(
                        rs.getInt("id"),
                        rs.getBigDecimal("price_per_hour"),
                        rs.getBoolean("available")
                );
                return Optional.of(ws);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching workspace by ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    public Workspace getCheapestAvailableWorkspace() {
        String sql = "SELECT * FROM workspaces WHERE available = true ORDER BY price_per_hour ASC LIMIT 1";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new Workspace(
                        rs.getInt("id"),
                        rs.getBigDecimal("price_per_hour"),
                        rs.getBoolean("available")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching cheapest available workspace: " + e.getMessage());
        }

        return null;
    }
}
