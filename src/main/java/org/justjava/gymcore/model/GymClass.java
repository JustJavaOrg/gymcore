package org.justjava.gymcore.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "classes", schema = "gym_app")
@Data
@NoArgsConstructor
public class GymClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(name = "scheduled_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledAt;

    @Column(name = "schedule_end")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduleEnd;

    private int capacity;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    public GymClass(String title, String description, LocalDateTime scheduledAt, LocalDateTime scheduledEnd, int capacity, User trainer) {
        this.title = title;
        this.description = description;
        this.scheduledAt = scheduledAt;
        this.scheduleEnd = scheduledEnd;
        this.capacity = capacity;
        this.trainer = trainer;
    }
}
