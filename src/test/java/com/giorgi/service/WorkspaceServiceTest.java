package com.giorgi.service;

import com.giorgi.model.Workspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class WorkspaceServiceTest {
    private WorkspaceService service;
    private Workspace testWorkspace;

    @BeforeEach
    void setUp() {
        service = new WorkspaceService();
        testWorkspace = new Workspace(1, BigDecimal.valueOf(15.0), true);
    }

    @Test
    void addWorkspace_ShouldMakeItAvailable() {
        service.addWorkspace(testWorkspace);

        assertFalse(service.getAllWorkspaces().isEmpty());
        assertDoesNotThrow(() -> {
            Workspace found = service.getWorkspaceById(1).orElseThrow();
            assertEquals(1, found.getId());
            assertEquals(BigDecimal.valueOf(15.0), found.getPricePerHour());
            assertTrue(found.isAvailable());
        });
    }

    @Test
    void removeWorkspace_ShouldDeleteIt() {
        service.addWorkspace(testWorkspace);
        assertTrue(service.removeWorkspaceById(1));
        assertTrue(service.getAllWorkspaces().isEmpty());
        assertFalse(service.getWorkspaceById(1).isPresent());
    }
}