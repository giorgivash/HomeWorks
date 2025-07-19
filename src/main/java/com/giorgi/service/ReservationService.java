package com.giorgi.service;

import com.giorgi.entity.*;
import com.giorgi.customExeptions.ReservationException;
import com.giorgi.model.ReservationModel;
import com.giorgi.repository.*;
import com.giorgi.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              WorkspaceRepository workspaceRepository,
                              CustomerRepository customerRepository) {
        this.reservationRepository = reservationRepository;
        this.workspaceRepository = workspaceRepository;
        this.customerRepository = customerRepository;
    }

    public List<ReservationModel> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ModelMapper::toModel)
                .toList();
    }

    public boolean createReservation(ReservationModel reservationModel) throws ReservationException {
        Customer customer = customerRepository.findById(reservationModel.getCustomerId())
                .orElseThrow(() -> new ReservationException("Customer not found"));

        Workspace workspace = workspaceRepository.findById(reservationModel.getWorkspaceId())
                .orElseThrow(() -> new ReservationException("Workspace not found"));

        if (!workspace.isAvailable()) {
            throw new ReservationException("Workspace is not available");
        }

        if (reservationRepository.existsConflictingReservation(
                workspace,
                reservationModel.getStartTime(),
                reservationModel.getEndTime())) {
            throw new ReservationException("Time slot conflicts with existing reservation");
        }

        Reservation reservation = new Reservation.Builder()
                .setCustomer(customer)
                .setWorkspace(workspace)
                .setStartTime(reservationModel.getStartTime())
                .setEndTime(reservationModel.getEndTime())
                .build();

        workspace.setAvailable(false);
        workspaceRepository.save(workspace);

        reservationRepository.save(reservation);
        return true;
    }

    public List<ReservationModel> getReservationsByCustomerId(Integer customerId) {
        return reservationRepository.findByCustomerId(customerId).stream()
                .map(ModelMapper::toModel)
                .toList();
    }

    public boolean cancelReservationById(Integer reservationId) {
        if (reservationRepository.existsById(reservationId)) {
            reservationRepository.deleteById(reservationId);
            return true;
        }
        return false;
    }
}