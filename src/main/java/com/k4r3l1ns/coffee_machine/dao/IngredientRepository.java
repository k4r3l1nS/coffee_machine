package com.k4r3l1ns.coffee_machine.dao;

import com.k4r3l1ns.coffee_machine.models.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query(
            """
                SELECT I
                FROM Ingredient I
                JOIN CoffeeIngredientTable CIT ON I.id = CIT.ingredient.id
                WHERE CIT.coffee.id = :id
            """
    )
    List<Ingredient> findByCoffeeId(@Param("id")Long id);

    boolean existsByIngredientNameIgnoreCase(String ingredientName);

    Ingredient findByIngredientNameIgnoreCase(String ingredientName);

    void deleteByIngredientNameIgnoreCase(String name);
}
