package com.k4r3l1ns.coffee_machine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {

    private String name;
    private double overallRevenue;
    private long orderCount;
    private double portionCount;
}
