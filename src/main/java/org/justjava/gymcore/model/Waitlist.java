package org.justjava.gymcore.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "waitlists")
@Data
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private GymClass gymClass;

    @Column(name = "waitlist_time")
    private LocalDateTime waitlistTime;

    public Waitlist() {
        this.waitlistTime = LocalDateTime.now();
    }

    public Waitlist(User user, GymClass gymClass) {
        this.user = user;
        this.gymClass = gymClass;
        this.waitlistTime = LocalDateTime.now();
    }
}
