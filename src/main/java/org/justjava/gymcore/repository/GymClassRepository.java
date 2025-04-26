package org.justjava.gymcore.repository;

import org.justjava.gymcore.model.GymClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface GymClassRepository extends JpaRepository<GymClass, Long> {

    @Query("""
    select count(g) = 0 from GymClass g
    where g.trainer.id = :userId
    and (g.startTime <= :endTime
    and g.endTime >= :startTime)
    """)
    boolean existsByUserIdAndBookingTimePeriod(@Param("userId") Long userId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTIme") LocalDateTime endTIme);
}
