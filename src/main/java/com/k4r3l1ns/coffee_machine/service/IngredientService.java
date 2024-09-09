package com.k4r3l1ns.coffee_machine.service;

import com.k4r3l1ns.coffee_machine.dao.CoffeeIngredientTableRepository;
import com.k4r3l1ns.coffee_machine.dao.IngredientRepository;
import com.k4r3l1ns.coffee_machine.dao.MeasurementRepository;
import com.k4r3l1ns.coffee_machine.dto.IngredientDto;
import com.k4r3l1ns.coffee_machine.models.Ingredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final MeasurementRepository measurementRepository;
    private final CoffeeIngredientTableRepository coffeeIngredientTableRepository;

    public void save(IngredientDto ingredientDto) {

        ingredientDto.throwIfInvalid();
        if (!measurementRepository.existsByName(ingredientDto.getMeasurement().toUpperCase())) {
            throw new NoSuchElementException(
                    "Measurement " + ingredientDto.getMeasurement() + " does not exist"
            );
        }
        if (ingredientRepository.existsByIngredientNameIgnoreCase(ingredientDto.getName())) {
            throw new RuntimeException("Ingredient " + ingredientDto.getName() + " already exists");
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName(ingredientDto.getName());
        ingredient.setMeasurement(
                measurementRepository.findByName(ingredientDto.getMeasurement().toUpperCase())
        );
        ingredient.setResidualValue(0);
        ingredientRepository.save(ingredient);
    }

    public void delete(String name) {

        if (!ingredientRepository.existsByIngredientNameIgnoreCase(name)) {
            throw new NoSuchElementException("Ingredient " + name + " does not exist");
        }
        var ingredient = ingredientRepository.findByIngredientNameIgnoreCase(name);
        if (coffeeIngredientTableRepository.existsByIngredient(ingredient)) {
            throw new RuntimeException("Ingredient " + name + " is used in recipies and cannot be deleted");
        }
        ingredientRepository.deleteByIngredientNameIgnoreCase(name);
    }

    /**
     * Изменить запас ингредиента
     *
     * @param ingredientName Название ингредиента
     * @param incrementedAmount Число, на которое следует изменить запас (может быть отрицательным)
     */
    public void increaseResiduals(
            String ingredientName,
            double incrementedAmount
    ) {
        var ingredient = ingredientRepository.findByIngredientNameIgnoreCase(ingredientName);
        if (ingredient == null) {
            throw new NoSuchElementException("Ingredient " + ingredientName + " does not exist");
        }
        if (ingredient.getResidualValue() + incrementedAmount < 0) {
            throw new RuntimeException(
                    "Residual value " + (ingredient.getResidualValue() + incrementedAmount) + " became less than zero"
            );
        }
        ingredient.setResidualValue(ingredient.getResidualValue() + incrementedAmount);
    }

    public String getResiduals(String ingredientName) {
        var ingredient = ingredientRepository.findByIngredientNameIgnoreCase(ingredientName);
        if (ingredient == null) {
            throw new NoSuchElementException("Ingredient " + ingredientName + " does not exist");
        }
        return ingredient.getResidualValue() + " " + ingredient.getMeasurement().getName();
    }
}
