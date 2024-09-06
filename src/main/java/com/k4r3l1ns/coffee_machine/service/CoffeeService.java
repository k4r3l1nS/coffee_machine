package com.k4r3l1ns.coffee_machine.service;

import com.k4r3l1ns.coffee_machine.dao.*;
import com.k4r3l1ns.coffee_machine.dto.CoffeeInfo;
import com.k4r3l1ns.coffee_machine.dto.RecipeDto;
import com.k4r3l1ns.coffee_machine.models.Coffee;
import com.k4r3l1ns.coffee_machine.models.CoffeeIngredientTable;
import com.k4r3l1ns.coffee_machine.models.Order;
import com.k4r3l1ns.coffee_machine.models.PriceList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public class CoffeeService {

    @Value("${coffee.portion-coefficient.max-value}")
    private double maxPortionValue;

    private final CoffeeRepository coffeeRepository;
    private final PriceListRepository priceListRepository;
    private final IngredientRepository ingredientRepository;
    private final CoffeeIngredientTableRepository coffeeIngredientTableRepository;
    private final OrderRepository orderRepository;

    /**
     * Получить всю информацию о данном напитке: название, описание,
     * объём, цену и список ингредиентов с их количеством
     *
     * @param name Название напитка
     * @return Информация о кофе
     */
    @Transactional(readOnly = true)
    public CoffeeInfo coffeeInfo(String name) {

        var coffee = coffeeRepository.findByName(name);
        if (coffee == null) {
            throw new NoSuchElementException("Coffee \"" + name + "\" not found");
        }

        var price = priceListRepository.findByCoffeeId(coffee.getId()).getPrice();
        var ingredientList = ingredientRepository.findByCoffeeId(coffee.getId());

        Map<String, String> ingredientMap = new HashMap<>();
        for (var ingredient : ingredientList) {
            ingredientMap.put(
                    ingredient.getIngredientName(),
                    coffeeIngredientTableRepository.getIngredientValueByIds(
                            coffee.getId(),
                            ingredient.getId()
                    ) + " " + ingredient.getMeasurement().getName()
            );
        }

        return CoffeeInfo.builder()
                .name(name)
                .description(coffee.getDescription())
                .volume(coffee.getVolume())
                .price(price)
                .ingredients(ingredientMap)
                .build();
    }

    /**
     * Отправить кофе в очередь сообщений Kafka
     *
     * @param name Название кофе
     * @param portionCoefficient Количество порций кофе
     */
    public void sendToQueue(
            String name,
            double portionCoefficient
    ) {
        if (portionCoefficient <= 0 || portionCoefficient > maxPortionValue) {
            throw new IllegalArgumentException("Invalid portion coefficient: " + portionCoefficient);
        }
        // todo -> Отправка в Kafka
    }

    /**
     * Приготовление кофе по названию и количеству порций
     *
     * @param name Название кофе
     * @param portionCoefficient Количество порций кофе
     */
    public void makeCoffee(
            String name,
            double portionCoefficient
    ) {
        var coffee = coffeeRepository.findByName(name);
        if (coffee == null) {
            throw new NoSuchElementException("Coffee \"" + name + "\" not found");
        }
        var ingredientList = ingredientRepository.findByCoffeeId(coffee.getId());

        /*
            Составим Map из ингредиентов, которых осталось недостаточно.
            Параллельно будем вычитать из остаточного значения требуемое.
            В случае, если остатков хватать не будет, транзакция откатится
        */
        Map<String, String> failedIngredientMap = new HashMap<>();
        for (var ingredient : ingredientList) {
            var requiredValue = coffeeIngredientTableRepository.getIngredientValueByIds(
                    coffee.getId(),
                    ingredient.getId()
            ) * portionCoefficient;
            var residualValue = ingredient.getResidualValue();
            if (residualValue < requiredValue) {
                failedIngredientMap.put(
                        ingredient.getIngredientName(),
                        residualValue + "  " + ingredient.getMeasurement().getName()
                );
            }
            ingredient.setResidualValue(residualValue - requiredValue);
        }

        // Проверим, есть ли такие ингредиенты
        if (!failedIngredientMap.isEmpty()) {
            StringBuilder context = new StringBuilder("There is not enough ingredients to make a coffee:\n");
            for (var ingredientName : failedIngredientMap.keySet()) {
                context.append(ingredientName)
                        .append(": ")
                        .append(failedIngredientMap.get(ingredientName))
                        .append(" left\n");
            }
            throw new RuntimeException(context.toString());
        }

        // Составим и сохраним заказ
        Order order = new Order();
        order.setCoffee(coffee);
        order.setPortionCoefficient(portionCoefficient);
        orderRepository.save(order);
    }

    public void saveRecipe(RecipeDto recipeDto) {

        // Проверка на корректность данных
        recipeDto.throwIfInvalid();
        for (var ingredientName : recipeDto.getIngredients().keySet()) {
            if (!ingredientRepository.existsByIngredientName(ingredientName)) {
                throw new NoSuchElementException("Ingredient \"" + ingredientName + "\" not found");
            }
        }

        // Сохраняем кофе
        var coffee = new Coffee();
        coffee.setName(recipeDto.getName());
        coffee.setDescription(recipeDto.getDescription());
        coffee.setVolume(recipeDto.getVolume());
        coffeeRepository.save(coffee);

        // Сохраняем цену
        var priceList = new PriceList();
        priceList.setCoffee(coffee);
        priceList.setPrice(recipeDto.getPrice());
        priceListRepository.save(priceList);

        // Сохраняем привязку ингредиентов к кофе
        for (var ingredientName : recipeDto.getIngredients().keySet()) {
            var coffeeIngredientTable = new CoffeeIngredientTable();
            coffeeIngredientTable.setCoffee(coffee);
            coffeeIngredientTable.setIngredient(
                    ingredientRepository.findByIngredientName(ingredientName)
            );
            coffeeIngredientTable.setIngredientValue(recipeDto.getIngredients().get(ingredientName));
            coffeeIngredientTableRepository.save(coffeeIngredientTable);
        }
    }

    public void deleteRecipe(String name) {
        if (!coffeeRepository.existsByName(name)) {
            throw new NoSuchElementException("Coffee \"" + name + "\" not found");
        }
        coffeeRepository.deleteByName(name);
    }
}
