package com.k4r3l1ns.coffee_machine.dao;

import com.k4r3l1ns.coffee_machine.models.CoffeeIngredientTable;
import com.k4r3l1ns.coffee_machine.models.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeIngredientTableRepository extends JpaRepository<CoffeeIngredientTable, Long> {

    @Query(
            """
                SELECT CIT.ingredientValue
                FROM CoffeeIngredientTable CIT
                WHERE CIT.coffee.id = :coffeeId
                  AND CIT.ingredient.id = :ingredientId
            """
    )
    double getIngredientValueByIds(Long coffeeId, Long ingredientId);

    boolean existsByIngredient(Ingredient ingredient);
}
