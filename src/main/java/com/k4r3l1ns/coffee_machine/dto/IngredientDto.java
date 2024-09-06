package com.k4r3l1ns.coffee_machine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDto {

    private String name;
    private String measurement;

    public void throwIfInvalid() {
        if (name == null || name.isEmpty() || measurement == null || measurement.isEmpty()) {
            throw new RuntimeException("Invalid ingredient");
        }
    }
}
