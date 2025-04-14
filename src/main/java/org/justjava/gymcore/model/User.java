package org.justjava.gymcore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String password;

    @ManyToOne
    @JoinColumn(name = "membership_type_id")
    private MembershipType membershipType;

    public User(String name, String email, String password, UserRole role, MembershipType membershipType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.membershipType = membershipType;
    }
}
