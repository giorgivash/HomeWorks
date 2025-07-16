package com.giorgi.config;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectorTest {

    @Test
    void testConnectionSuccess() {
        try (Connection conn = DBConnector.getConnection()) {
            assertNotNull(conn);
            System.out.println("Connection to database was successful.");
        } catch (SQLException e) {
            fail("Connection failed: " + e.getMessage());
        }
    }
}
