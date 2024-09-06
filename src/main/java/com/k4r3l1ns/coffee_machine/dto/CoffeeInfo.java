package com.k4r3l1ns.coffee_machine.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeInfo {

    private String name;
    private String description;
    private double volume;
    private double price;
    private Map<String, String> ingredients;
}
