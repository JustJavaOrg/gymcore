package org.justjava.gymcore.repository;

import org.justjava.gymcore.model.Booking;
import org.justjava.gymcore.model.GymClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Count confirmed bookings for a class
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.gymClass.id = :gymClassId")
    long countByGymClassId(@Param("gymClassId") Long gymClassId);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.gymClass.trainer.id = :trainerId
          AND (:startTime < b.gymClass.scheduleEnd AND :endTime > b.gymClass.scheduledAt)
    """)
    List<Booking> findOverlappingTrainerBookings(@Param("trainerId") Long trainerId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

}
