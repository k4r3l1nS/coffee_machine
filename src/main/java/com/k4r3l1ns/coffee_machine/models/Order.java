package com.k4r3l1ns.coffee_machine.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private double portionCoefficient;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime orderDate;

    @ManyToOne
    private Coffee coffee;
}
