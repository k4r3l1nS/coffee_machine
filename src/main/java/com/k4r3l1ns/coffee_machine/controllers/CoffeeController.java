package com.k4r3l1ns.coffee_machine.controllers;

import com.k4r3l1ns.coffee_machine.dto.CoffeeInfo;
import com.k4r3l1ns.coffee_machine.dto.OrderDto;
import com.k4r3l1ns.coffee_machine.dto.RecipeDto;
import com.k4r3l1ns.coffee_machine.service.CoffeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${coffee.url}")
public class CoffeeController {

    private final CoffeeService coffeeService;

    @GetMapping("/info")
    public ResponseEntity<CoffeeInfo> getCoffeeInfo(String name) {
        return ResponseEntity.ok(coffeeService.coffeeInfo(name));
    }

    @PostMapping("/make")
    public ResponseEntity<String> makeCoffee(
            @RequestBody OrderDto orderDto
    ) {
        coffeeService.sendToQueue(orderDto);
        return ResponseEntity.ok("Order is processed and sent to the queue");
    }

    @PostMapping("/new-recipe")
    public ResponseEntity<String> newRecipe(
            @RequestBody RecipeDto recipeDto
    ) {
        coffeeService.saveRecipe(recipeDto);
        return ResponseEntity.ok("Recipe was successfully saved");
    }

    @DeleteMapping("/delete-recipe")
    public ResponseEntity<String> deleteRecipe(
            @RequestParam String name
    ) {
        coffeeService.deleteRecipe(name);
        return ResponseEntity.ok("Recipe was successfully deleted");
    }
}
