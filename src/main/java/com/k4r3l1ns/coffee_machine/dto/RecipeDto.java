package com.k4r3l1ns.coffee_machine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {

    private String name;
    private String description;
    private double volume;
    private double price;
    private Map<String, Double> ingredients;

    /**
     * Проверка на валидность данных
     */
    public void throwIfInvalid() {

        if (
                name == null || name.isEmpty() ||
                volume <= 0 || price <= 0 ||
                ingredients == null || ingredients.isEmpty()
        ) {
            throw new IllegalArgumentException("Recipe is invalid");
        }

        for (var ingredientName : ingredients.keySet()) {
            if (
                    ingredients.get(ingredientName) == null || ingredients.get(ingredientName) <= 0 ||
                    ingredientName.matches("\\d+")
            ) {
                throw new IllegalArgumentException("Invalid ingredient: " + ingredientName);
            }
        }
    }
}
