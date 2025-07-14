package com.giorgi.service;

import com.giorgi.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    void createReservation_ShouldFail_WhenWorkspaceIsBooked() throws Exception {

        Workspace bookedWorkspace = new Workspace(1, BigDecimal.TEN, false);
        when(workspaceService.getWorkspaceById(1)).thenReturn(Optional.of(bookedWorkspace));


        assertThrows(ReservationException.class, () ->
                reservationService.createReservation(1, 1, "Test", "2023-01-01", "09:00", "10:00")
        );
    }

    @Test
    void cancelReservation_ShouldFreeWorkspace() {

        Workspace ws = new Workspace(1, BigDecimal.TEN, false);
        Reservation res = new Reservation.Builder()
                .setId(1)
                .setCustomer(new Customer("Test", 1))
                .setWorkspace(ws)
                .setStartTime(LocalDateTime.now())
                .setEndTime(LocalDateTime.now().plusHours(1))
                .build();
        reservationService.addReservationFromFile(res);


        boolean result = reservationService.cancelReservationById(1, 1);


        assertTrue(result);
        assertTrue(ws.isAvailable());
    }
}