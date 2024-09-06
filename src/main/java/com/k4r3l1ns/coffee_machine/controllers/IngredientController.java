package com.k4r3l1ns.coffee_machine.controllers;

import com.k4r3l1ns.coffee_machine.dto.IngredientDto;
import com.k4r3l1ns.coffee_machine.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${ingredient.url}")
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    public ResponseEntity<String> addIngredient(
            @RequestBody IngredientDto ingredientDto
    ) {
        ingredientService.save(ingredientDto);
        return ResponseEntity.ok("Ingredient successfully added");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteIngredient(
            @RequestParam String name
    ) {
        ingredientService.delete(name);
        return ResponseEntity.ok("Ingredient successfully deleted");
    }

    @PostMapping("/residual/increase")
    public ResponseEntity<String> increaseResiduals(
            @RequestParam String ingredientName,
            @RequestParam double incrementedAmount
    ) {
        ingredientService.increaseResiduals(
                ingredientName,
                incrementedAmount
        );
        return ResponseEntity.ok("Residual amount successfully changed");
    }

    @GetMapping("/residual")
    public ResponseEntity<String> getResiduals(
            @RequestParam String ingredientName
    ) {
        return ResponseEntity.ok(
                "Residual for " + ingredientName + ": " + ingredientService.getResiduals(ingredientName)
        );
    }
}
