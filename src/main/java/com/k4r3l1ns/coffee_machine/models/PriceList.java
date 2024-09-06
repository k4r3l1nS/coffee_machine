package com.k4r3l1ns.coffee_machine.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "coffee_prices")
public class PriceList {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private double price;

    @OneToOne
    @JoinColumn(name = "coffee_id")
    private Coffee coffee;
}
