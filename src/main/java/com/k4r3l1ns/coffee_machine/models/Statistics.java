package com.k4r3l1ns.coffee_machine.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Getter
@Entity
@Immutable
@Subselect("SELECT * FROM v_coffee_stats")
public class Statistics {

    @Id
    private Long id;

    private String name;

    private double overallRevenue;

    private long orderCount;

    private double portionCount;
}
