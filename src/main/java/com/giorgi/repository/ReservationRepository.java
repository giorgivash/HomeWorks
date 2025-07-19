package com.giorgi.repository;

import com.giorgi.entity.Reservation;
import com.giorgi.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByCustomerId(Integer customerId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reservation r WHERE " +
            "r.workspace = :workspace AND " +
            "r.startTime < :end AND " +
            "r.endTime > :start")
    boolean existsConflictingReservation(
            @Param("workspace") Workspace workspace,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}