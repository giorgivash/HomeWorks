package com.giorgi.service;

import com.giorgi.config.DBConnector;
import com.giorgi.model.Workspace;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

class WorkspaceServiceTest {
    private WorkspaceService service;
    private Workspace testWorkspace;

    @BeforeEach
    void setUp() {
        service = new WorkspaceService();
        testWorkspace = new Workspace(999, BigDecimal.valueOf(25.5), true);
    }

    @Test
    void testAddAndRemoveWorkspace() {
        service.addWorkspace(testWorkspace);
        assertTrue(service.getWorkspaceById(999).isPresent());

        boolean removed = service.removeWorkspaceById(999);
        assertTrue(removed);
        assertFalse(service.getWorkspaceById(999).isPresent());
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM workspaces WHERE id = 999");
        }
    }
}