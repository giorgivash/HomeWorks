package service;

import model.Workspace;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceService {
    private final List<Workspace> workspaces = new ArrayList<>();
    private static final String FILE_NAME = "workspaces.txt";

    public WorkspaceService() {
        loadWorkspacesFromFile();
    }

    public void addWorkspace(Workspace workspace) {
        workspaces.add(workspace);
        saveWorkspacesToFile();
    }

    public boolean removeWorkspaceById(int id) {
        for (Workspace ws : workspaces) {
            if (ws.getID() == id) {
                workspaces.remove(ws);
                saveWorkspacesToFile();
                return true;
            }
        }
        return false;
    }

    public List<Workspace> getAllWorkspaces() {
        return new ArrayList<>(workspaces);
    }

    public List<Workspace> getAvailableWorkspaces() {
        List<Workspace> available = new ArrayList<>();
        for (Workspace ws : workspaces) {
            if (ws.isAvailable()) {
                available.add(ws);
            }
        }
        return available;
    }

    public Workspace getWorkspaceById(int workspaceId) {
        for (Workspace workspace : workspaces) {
            if (workspace.getID() == workspaceId) {
                return workspace;
            }
        }
        return null;
    }

    private void saveWorkspacesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Workspace ws : workspaces) {
                writer.write(ws.getID() + "," + ws.getPricePerHour() + "," + ws.isAvailable());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving workspaces to file: " + e.getMessage());
        }
    }

    private void loadWorkspacesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    BigDecimal price = new BigDecimal(parts[1]);
                    boolean available = Boolean.parseBoolean(parts[2]);
                    workspaces.add(new Workspace(id, price, available));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading workspaces from file: " + e.getMessage());
        }
    }
}
