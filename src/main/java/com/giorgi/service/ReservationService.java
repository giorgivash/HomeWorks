package com.giorgi.service;

import com.giorgi.config.JPAUtil;
import com.giorgi.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReservationService {
    private final WorkspaceService workspaceService;

    public ReservationService(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    public boolean createReservation(Customer customer, Workspace workspace,
                                     LocalDateTime start, LocalDateTime end) throws ReservationException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Reattach entities to current persistence context
            Customer managedCustomer = em.merge(customer);
            Workspace managedWorkspace = em.merge(workspace);

            if (!managedWorkspace.isAvailable()) {
                throw new ReservationException("Workspace is not available.");
            }

            if (!end.isAfter(start)) {
                throw new ReservationException("End time must be after start time.");
            }

            if (reservationExists(managedWorkspace, start, end)) {
                throw new ReservationException("Time slot conflicts with existing reservation.");
            }

            Reservation reservation = new Reservation.Builder()
                    .setCustomer(managedCustomer)
                    .setWorkspace(managedWorkspace)
                    .setStartTime(start)
                    .setEndTime(end)
                    .build();

            em.persist(reservation);
            managedWorkspace.setAvailable(false);
            em.merge(managedWorkspace);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ReservationException("Error creating reservation: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public List<Reservation> getReservationsByCustomerId(Integer customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Reservation> query = em.createQuery(
                    "SELECT r FROM Reservation r WHERE r.customer.id = :customerId", Reservation.class);
            query.setParameter("customerId", customerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean cancelReservationById(Integer reservationId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Reservation reservation = em.find(Reservation.class, reservationId);
            if (reservation == null) {
                return false;
            }

            Workspace workspace = em.merge(reservation.getWorkspace());
            workspace.setAvailable(true);

            em.remove(reservation);
            em.merge(workspace);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }

    public List<Reservation> getAllReservations() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Reservation> query = em.createQuery(
                    "SELECT r FROM Reservation r", Reservation.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    private boolean reservationExists(Workspace workspace, LocalDateTime start, LocalDateTime end) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Reservation> root = cq.from(Reservation.class);

            Predicate workspacePredicate = cb.equal(root.get("workspace"), workspace);
            Predicate overlapPredicate = cb.and(
                    cb.lessThan(root.get("startTime"), end),
                    cb.greaterThan(root.get("endTime"), start)
            );

            cq.select(cb.count(root))
                    .where(cb.and(workspacePredicate, overlapPredicate));

            Long count = em.createQuery(cq).getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}