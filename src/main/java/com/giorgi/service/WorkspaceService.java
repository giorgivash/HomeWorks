package com.giorgi.service;

import com.giorgi.config.JPAUtil;
import com.giorgi.model.Workspace;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class WorkspaceService {
    public void addWorkspace(Workspace workspace) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(workspace);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public boolean removeWorkspaceById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Workspace workspace = em.find(Workspace.class, id);
            if (workspace != null) {
                em.remove(workspace);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } finally {
            em.close();
        }
    }

    public List<Workspace> getAllWorkspaces() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Workspace> query = em.createQuery(
                    "SELECT w FROM Workspace w", Workspace.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Workspace> getWorkspaceById(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Workspace workspace = em.find(Workspace.class, id);
            return Optional.ofNullable(workspace);
        } finally {
            em.close();
        }
    }

    public List<Workspace> getAvailableWorkspaces() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Workspace> query = em.createQuery(
                    "SELECT w FROM Workspace w WHERE w.available = true",
                    Workspace.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}