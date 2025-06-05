package service;

import model.Workspace;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceService {
    private final List<Workspace> workspaces = new ArrayList<>();


    public void addWorkspace(Workspace workspace) {
        workspaces.add(workspace);
    }


    public boolean removeWorkspaceById(int id) {
        for (Workspace ws : workspaces) {
            if (ws.getID() == id) {
                workspaces.remove(ws);
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



    public Workspace findById(int id) {
        for (Workspace ws : workspaces) {
            if (ws.getID() == id) {
                return ws;
            }
        }
        return null;
    }

    public Workspace getWorkspaceById(int workspaceId) {
        for (Workspace workspace : workspaces) {
            if (workspace.getID() == workspaceId) {
                return workspace;
            }
        }
        return null;
    }

}
