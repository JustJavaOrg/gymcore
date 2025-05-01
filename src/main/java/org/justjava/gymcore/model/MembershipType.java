package org.justjava.gymcore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "membership_type")
@Data
@NoArgsConstructor
public class MembershipType {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String description;
        private BigDecimal price;

        public MembershipType(String name, String description, BigDecimal price) {
                this.name = name;
                this.description = description;
                this.price = price;
        }
}
