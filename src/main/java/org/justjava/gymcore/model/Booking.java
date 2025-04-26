package org.justjava.gymcore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Table(name = "bookings", schema = "gym_app", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "class_id"})
})
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private GymClass gymClass;

    @Column(name = "booking_time")
    private LocalDateTime bookingTime;

    public Booking() {
        this.bookingTime = LocalDateTime.now();
    }

    public Booking(User user, GymClass gymClass) {
        this.user = user;
        this.gymClass = gymClass;
        this.bookingTime = LocalDateTime.now();
    }

}
