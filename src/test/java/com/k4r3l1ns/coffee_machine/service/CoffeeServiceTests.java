package com.k4r3l1ns.coffee_machine.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.k4r3l1ns.coffee_machine.dao.*;
import com.k4r3l1ns.coffee_machine.dto.CoffeeInfo;
import com.k4r3l1ns.coffee_machine.dto.OrderDto;
import com.k4r3l1ns.coffee_machine.dto.RecipeDto;
import com.k4r3l1ns.coffee_machine.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeServiceTests {

    @Mock
    private CoffeeRepository coffeeRepository;

    @Mock
    private PriceListRepository priceListRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private CoffeeIngredientTableRepository coffeeIngredientTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CoffeeService coffeeService;

    @Test
    void testCoffeeInfo() {

        String coffeeName = "Latte";
        Coffee coffee = new Coffee();
        coffee.setName(coffeeName);
        coffee.setDescription("Delicious latte");
        coffee.setVolume(250);

        PriceList priceList = new PriceList();
        priceList.setPrice(200);

        Measurement measurement = new Measurement();
        measurement.setName("ML");
        measurement.setContext("Millilitres");

        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName("Milk");
        ingredient.setMeasurement(measurement);

        CoffeeIngredientTable coffeeIngredientTable = new CoffeeIngredientTable();
        coffeeIngredientTable.setIngredientValue(100);

        when(coffeeRepository.findByNameIgnoreCase(coffeeName)).thenReturn(coffee);
        when(priceListRepository.findByCoffeeId(coffee.getId())).thenReturn(priceList);
        when(ingredientRepository.findByCoffeeId(coffee.getId())).thenReturn(Collections.singletonList(ingredient));
        when(coffeeIngredientTableRepository.getIngredientValueByIds(coffee.getId(), ingredient.getId()))
                .thenReturn(coffeeIngredientTable.getIngredientValue());

        CoffeeInfo coffeeInfo = coffeeService.coffeeInfo(coffeeName);

        assertNotNull(coffeeInfo);
        assertEquals(coffeeName, coffeeInfo.getName());
        assertEquals("Delicious latte", coffeeInfo.getDescription());
        assertEquals(250, coffeeInfo.getVolume());
        assertEquals(200, coffeeInfo.getPrice());
        assertTrue(coffeeInfo.getIngredients().containsKey("Milk"));
        assertEquals("100.0 ML", coffeeInfo.getIngredients().get("Milk"));
    }

    @Test
    void testMakeCoffee() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCoffeeName("Latte");
        orderDto.setPortionCoefficient(2.0);

        Coffee coffee = new Coffee();
        coffee.setId(1L);
        coffee.setName("Espresso");

        Measurement measurement = new Measurement();
        measurement.setName("ML");
        measurement.setContext("Millilitres");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setIngredientName("Milk");
        ingredient.setResidualValue(5000);
        ingredient.setMeasurement(measurement);

        CoffeeIngredientTable coffeeIngredientTable = new CoffeeIngredientTable();
        coffeeIngredientTable.setIngredientValue(10000);

        when(coffeeRepository.findByNameIgnoreCase(orderDto.getCoffeeName())).thenReturn(new Coffee());
        when(ingredientRepository.findByCoffeeId(coffee.getId())).thenReturn(Collections.singletonList(ingredient));
        when(coffeeIngredientTableRepository.getIngredientValueByIds(coffee.getId(), ingredient.getId()))
                .thenReturn(coffeeIngredientTable.getIngredientValue());

        coffeeService.makeCoffee(orderDto);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testSendToQueue() {
        OrderDto orderDto = new OrderDto();
        orderDto.setCoffeeName("Latte");
        orderDto.setPortionCoefficient(2.0);

        assertThrows(IllegalArgumentException.class, () -> coffeeService.sendToQueue(orderDto));
    }

    @Test
    void testSaveRecipe() {

        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("Latte");
        recipeDto.setDescription("Delicious latte");
        recipeDto.setVolume(250);
        recipeDto.setPrice(5);
        recipeDto.setIngredients(Map.of("Milk", 100.0));

        when(ingredientRepository.existsByIngredientNameIgnoreCase("Milk")).thenReturn(true);

        coffeeService.saveRecipe(recipeDto);

        verify(coffeeRepository, times(1)).save(any(Coffee.class));
        verify(priceListRepository, times(1)).save(any(PriceList.class));
        verify(coffeeIngredientTableRepository, times(1)).save(any(CoffeeIngredientTable.class));
    }

    @Test
    void testDeleteRecipe() {
        String coffeeName = "Latte";
        when(coffeeRepository.existsByNameIgnoreCase(coffeeName)).thenReturn(true);
        coffeeService.deleteRecipe(coffeeName);
        verify(coffeeRepository, times(1)).deleteByNameIgnoreCase(coffeeName);
    }

    @Test
    void testDeleteRecipeNotFound() {
        String coffeeName = "Latte";
        when(coffeeRepository.existsByNameIgnoreCase(coffeeName)).thenReturn(false);
        Exception ex = assertThrows(
                NoSuchElementException.class,
                () -> coffeeService.deleteRecipe(coffeeName)
        );
        assertEquals("Coffee \"" + coffeeName + "\" not found", ex.getMessage());
    }
}
