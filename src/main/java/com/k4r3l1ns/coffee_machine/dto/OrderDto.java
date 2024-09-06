package com.k4r3l1ns.coffee_machine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String coffeeName;
    private Double portionCoefficient;

    public void throwIfInvalid() {
        if (coffeeName == null || coffeeName.isEmpty()) {
            throw new IllegalArgumentException("Coffee name cannot be empty");
        }
        if (portionCoefficient == null) {
            throw new IllegalArgumentException("Portion coefficient cannot be null");
        }
    }
}
