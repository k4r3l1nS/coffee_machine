package com.k4r3l1ns.coffee_machine.service;

import com.k4r3l1ns.coffee_machine.dao.CoffeeIngredientTableRepository;
import com.k4r3l1ns.coffee_machine.dao.IngredientRepository;
import com.k4r3l1ns.coffee_machine.dao.MeasurementRepository;
import com.k4r3l1ns.coffee_machine.dto.IngredientDto;
import com.k4r3l1ns.coffee_machine.models.Ingredient;
import com.k4r3l1ns.coffee_machine.models.Measurement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTests {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private CoffeeIngredientTableRepository coffeeIngredientTableRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void testSaveIngredientSuccess() {
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Sugar");
        ingredientDto.setMeasurement("grams");

        Measurement measurement = new Measurement();
        measurement.setName("GRAMS");

        when(measurementRepository.existsByName("GRAMS")).thenReturn(true);
        when(ingredientRepository.existsByIngredientName("Sugar")).thenReturn(false);
        when(measurementRepository.findByName("GRAMS")).thenReturn(measurement);

        ingredientService.save(ingredientDto);

        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void testSaveIngredientMeasurementNotFound() {
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Sugar");
        ingredientDto.setMeasurement("grams");

        when(measurementRepository.existsByName("GRAMS")).thenReturn(false);

        NoSuchElementException thrown = assertThrows(
                NoSuchElementException.class,
                () -> ingredientService.save(ingredientDto)
        );

        assertEquals("Measurement grams does not exist", thrown.getMessage());
    }

    @Test
    void testSaveIngredientAlreadyExists() {
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Sugar");
        ingredientDto.setMeasurement("grams");

        when(measurementRepository.existsByName("GRAMS")).thenReturn(true);
        when(ingredientRepository.existsByIngredientName("Sugar")).thenReturn(true);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> ingredientService.save(ingredientDto)
        );

        assertEquals("Ingredient Sugar already exists", thrown.getMessage());
    }

    @Test
    void testDeleteIngredientSuccess() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName("Sugar");

        when(ingredientRepository.existsByIngredientName("Sugar")).thenReturn(true);
        when(ingredientRepository.findByIngredientName("Sugar")).thenReturn(ingredient);
        when(coffeeIngredientTableRepository.existsByIngredient(ingredient)).thenReturn(false);

        ingredientService.delete("Sugar");

        verify(ingredientRepository, times(1)).deleteByIngredientName("Sugar");
    }

    @Test
    void testDeleteIngredientNotFound() {
        when(ingredientRepository.existsByIngredientName("Sugar")).thenReturn(false);

        NoSuchElementException thrown = assertThrows(
                NoSuchElementException.class,
                () -> ingredientService.delete("Sugar")
        );

        assertEquals("Ingredient Sugar does not exist", thrown.getMessage());
    }

    @Test
    void testDeleteIngredientUsedInRecipes() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName("Sugar");

        when(ingredientRepository.existsByIngredientName("Sugar")).thenReturn(true);
        when(ingredientRepository.findByIngredientName("Sugar")).thenReturn(ingredient);
        when(coffeeIngredientTableRepository.existsByIngredient(ingredient)).thenReturn(true);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> ingredientService.delete("Sugar")
        );

        assertEquals("Ingredient Sugar is used in recipies and cannot be deleted", thrown.getMessage());
    }

    @Test
    void testIncreaseResidualsSuccess() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName("Sugar");
        ingredient.setResidualValue(100.0);

        when(ingredientRepository.findByIngredientName("Sugar")).thenReturn(ingredient);

        ingredientService.increaseResiduals("Sugar", 50.0);

        assertEquals(150.0, ingredient.getResidualValue());
        verify(ingredientRepository, times(1)).findByIngredientName("Sugar");
    }

    @Test
    void testIncreaseResidualsIngredientNotFound() {
        when(ingredientRepository.findByIngredientName("Sugar")).thenReturn(null);

        NoSuchElementException thrown = assertThrows(
                NoSuchElementException.class,
                () -> ingredientService.increaseResiduals("Sugar", 50.0)
        );

        assertEquals("Ingredient Sugar does not exist", thrown.getMessage());
    }

    @Test
    void testIncreaseResidualsNegativeValue() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName("Sugar");
        ingredient.setResidualValue(100.0);

        when(ingredientRepository.findByIngredientName("Sugar")).thenReturn(ingredient);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> ingredientService.increaseResiduals("Sugar", -200.0)
        );

        assertEquals("Residual value -100.0 became less than zero", thrown.getMessage());
    }

    @Test
    void testGetResidualsSuccess() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName("Sugar");
        ingredient.setResidualValue(100.0);
        Measurement measurement = new Measurement();
        measurement.setName("grams");
        ingredient.setMeasurement(measurement);

        when(ingredientRepository.findByIngredientName("Sugar")).thenReturn(ingredient);

        String residuals = ingredientService.getResiduals("Sugar");

        assertEquals("100.0 grams", residuals);
    }

    @Test
    void testGetResidualsNotFound() {
        when(ingredientRepository.findByIngredientName("Sugar")).thenReturn(null);

        NoSuchElementException thrown = assertThrows(
                NoSuchElementException.class,
                () -> ingredientService.getResiduals("Sugar")
        );

        assertEquals("Ingredient Sugar does not exist", thrown.getMessage());
    }
}
