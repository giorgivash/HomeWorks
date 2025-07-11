package service;

import model.Workspace;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class WorkspaceService {
    private final Map<Integer, Workspace> workspacesMap = new HashMap<>();
    private final PriorityQueue<Workspace> workspacesByPrice = new PriorityQueue<>(
            Comparator.comparing(Workspace::getPricePerHour)
    );
    private static final String FILE_NAME = "workspaces.txt";

    public void addWorkspace(Workspace workspace) {
        workspacesMap.put(workspace.getID(), workspace);
        workspacesByPrice.add(workspace);
        saveWorkspacesToFile();
    }

    public boolean removeWorkspaceById(int id) {
        Workspace removed = workspacesMap.remove(id);
        if (removed != null) {
            workspacesByPrice.remove(removed);
            saveWorkspacesToFile();
            return true;
        }
        return false;
    }

    public List<Workspace> getAllWorkspaces() {
        return new ArrayList<>(workspacesMap.values());
    }

    public List<Workspace> getAvailableWorkspaces() {
        return workspacesMap.values().stream()
                .filter(Workspace::isAvailable)
                .collect(Collectors.toList());
    }

    public Optional<Workspace> getWorkspaceById(int workspaceId) {
        return Optional.ofNullable(workspacesMap.get(workspaceId));
    }

    public Workspace getCheapestAvailableWorkspace() {
        return workspacesByPrice.stream()
                .filter(Workspace::isAvailable)
                .findFirst()
                .orElse(null);
    }

    public void saveWorkspacesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            workspacesMap.values().forEach(ws -> {
                try {
                    writer.write(ws.getID() + "," + ws.getPricePerHour() + "," + ws.isAvailable());
                    writer.newLine();
                } catch (IOException e) {
                    System.err.println("Error writing workspace: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("Error saving workspaces to file: " + e.getMessage());
        }
    }

    public void loadWorkspacesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines()
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length == 3)
                    .forEach(parts -> {
                        int id = Integer.parseInt(parts[0]);
                        BigDecimal price = new BigDecimal(parts[1]);
                        boolean available = Boolean.parseBoolean(parts[2]);
                        Workspace ws = new Workspace(id, price, available);
                        workspacesMap.put(id, ws);
                        workspacesByPrice.add(ws);
                    });
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading workspaces from file: " + e.getMessage());
        }
    }
}