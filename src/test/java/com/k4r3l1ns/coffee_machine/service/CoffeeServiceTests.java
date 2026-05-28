package com.k4r3l1ns.coffee_machine.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.k4r3l1ns.coffee_machine.dao.*;
import com.k4r3l1ns.coffee_machine.dto.CoffeeInfo;
import com.k4r3l1ns.coffee_machine.dto.OrderDto;
import com.k4r3l1ns.coffee_machine.dto.RecipeDto;
import com.k4r3l1ns.coffee_machine.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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

    @Mock
    private KafkaTemplate<Object, Object> kafkaTemplate;

    private CoffeeService coffeeService;

    @BeforeEach
    public void setUp() {
        coffeeService = new CoffeeService(
                1.0,
                "coffee-topic",
                kafkaTemplate,
                coffeeRepository,
                priceListRepository,
                ingredientRepository,
                coffeeIngredientTableRepository,
                orderRepository
        );
    }

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
    void makeCoffee_shouldSaveOrder() {
        Coffee coffee = new Coffee();
        coffee.setId(1L);

        Measurement measurement = new Measurement();
        measurement.setName("ml");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(2L);
        ingredient.setIngredientName("Milk");
        ingredient.setResidualValue(500.0);
        ingredient.setMeasurement(measurement);

        OrderDto dto = new OrderDto();
        dto.setCoffeeName("Latte");
        dto.setPortionCoefficient(2.0);

        when(coffeeRepository.findByNameIgnoreCase("Latte"))
                .thenReturn(coffee);
        when(ingredientRepository.findByCoffeeId(1L))
                .thenReturn(List.of(ingredient));
        when(coffeeIngredientTableRepository.getIngredientValueByIds(1L, 2L))
                .thenReturn(100.0);

        coffeeService.makeCoffee(dto);

        assertEquals(300.0, ingredient.getResidualValue());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void makeCoffee_shouldNotSaveOrderWhenIngredientsNotEnough() {
        Coffee coffee = new Coffee();
        coffee.setId(1L);

        Measurement measurement = new Measurement();
        measurement.setName("ml");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(2L);
        ingredient.setIngredientName("Milk");
        ingredient.setResidualValue(50.0);
        ingredient.setMeasurement(measurement);

        OrderDto dto = new OrderDto();
        dto.setCoffeeName("Latte");
        dto.setPortionCoefficient(1.0);

        when(coffeeRepository.findByNameIgnoreCase("Latte"))
                .thenReturn(coffee);
        when(ingredientRepository.findByCoffeeId(1L))
                .thenReturn(List.of(ingredient));
        when(coffeeIngredientTableRepository.getIngredientValueByIds(1L, 2L))
                .thenReturn(100.0);

        coffeeService.makeCoffee(dto);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void sendToQueue_shouldSendMessage() {
        OrderDto dto = new OrderDto();
        dto.setCoffeeName("Latte");
        dto.setPortionCoefficient(1.0);

        when(kafkaTemplate.send(anyString(), any())).thenReturn(mock(CompletableFuture.class));

        coffeeService.sendToQueue(dto);

        verify(kafkaTemplate).send("coffee-topic", dto);
    }

    @Test
    void sendToQueue_shouldThrowForInvalidPortion() {
        OrderDto dto = new OrderDto();
        dto.setCoffeeName("Latte");
        dto.setPortionCoefficient(10.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> coffeeService.sendToQueue(dto)
        );

        verifyNoInteractions(kafkaTemplate);
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
