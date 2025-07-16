package com.giorgi.config;

import com.giorgi.model.Workspace;
import com.giorgi.model.Reservation;
import com.giorgi.model.Customer;
import com.giorgi.service.ReservationService;
import com.giorgi.service.WorkspaceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DataLoader {
    public static boolean loadData(WorkspaceService workspaceService, ReservationService reservationService) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            boolean customersLoaded = ensureCustomerExists(em);
            boolean workspacesLoaded = loadWorkspaces(em);
            boolean reservationsLoaded = loadReservations(em, workspaceService, reservationService);
            em.getTransaction().commit();
            return customersLoaded && workspacesLoaded && reservationsLoaded;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error loading data: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    private static boolean ensureCustomerExists(EntityManager em) {
        try {
            Long count = em.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.name = 'CustomerUser'", Long.class)
                    .getSingleResult();
            if (count == 0) {
                Customer customer = new Customer("CustomerUser");
                em.persist(customer);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error ensuring customer exists: " + e.getMessage());
            return false;
        }
    }

    private static boolean loadWorkspaces(EntityManager em) {
        try {
            Long count = em.createQuery("SELECT COUNT(w) FROM Workspace w", Long.class).getSingleResult();
            if (count == 0) {
                Workspace ws1 = new Workspace();
                ws1.setPricePerHour(new BigDecimal("15.00"));
                ws1.setAvailable(true);
                em.persist(ws1);

                Workspace ws2 = new Workspace();
                ws2.setPricePerHour(new BigDecimal("20.00"));
                ws2.setAvailable(true);
                em.persist(ws2);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error loading workspaces: " + e.getMessage());
            return false;
        }
    }

    private static boolean loadReservations(EntityManager em, WorkspaceService workspaceService,
                                            ReservationService reservationService) {
        try {
            TypedQuery<Reservation> query = em.createQuery(
                    "SELECT r FROM Reservation r JOIN FETCH r.customer", Reservation.class);
            List<Reservation> reservations = query.getResultList();

            for (Reservation res : reservations) {
                Workspace workspace = em.find(Workspace.class, res.getWorkspace().getId());
                if (workspace != null) {
                    workspace.setAvailable(false);
                    em.merge(workspace);
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error loading reservations: " + e.getMessage());
            return false;
        }
    }
}