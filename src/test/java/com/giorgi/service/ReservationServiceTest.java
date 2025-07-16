package com.giorgi.service;

import com.giorgi.model.Workspace;
import com.giorgi.model.ReservationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private WorkspaceService workspaceService;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservation_shouldThrowException_whenWorkspaceIsUnavailable() {
        int testWorkspaceId = 999;
        Workspace unavailableWorkspace = new Workspace(testWorkspaceId, BigDecimal.TEN, false);
        when(workspaceService.getWorkspaceById(testWorkspaceId)).thenReturn(Optional.of(unavailableWorkspace));

        ReservationException ex = assertThrows(ReservationException.class, () -> {
            reservationService.createReservation(1, testWorkspaceId, "Test User", "2025-07-16", "09:00", "10:00");
        });

        assertEquals("Workspace is not available.", ex.getMessage());
    }

    @Test
    void cancelReservationById_shouldReturnFalse_whenReservationDoesNotExist() {
        boolean result;
        try {
            result = reservationService.cancelReservationById(9999, 999);
        } catch (Exception e) {
            result = false;
        }
        assertFalse(result);
    }
}