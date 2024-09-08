package com.k4r3l1ns.coffee_machine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k4r3l1ns.coffee_machine.config.NoSecurityConfig;
import com.k4r3l1ns.coffee_machine.dto.CoffeeInfo;
import com.k4r3l1ns.coffee_machine.dto.OrderDto;
import com.k4r3l1ns.coffee_machine.dto.RecipeDto;
import com.k4r3l1ns.coffee_machine.handler.CustomExceptionHandler;
import com.k4r3l1ns.coffee_machine.service.CoffeeService;
import com.k4r3l1ns.coffee_machine.service.JwtService;
import com.k4r3l1ns.coffee_machine.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoffeeController.class)
@Import({CustomExceptionHandler.class, NoSecurityConfig.class})
public class CoffeeControllerTests {

    @MockBean
    private CoffeeService coffeeService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetCoffeeInfoSuccess() throws Exception {
        CoffeeInfo coffeeInfo = new CoffeeInfo("Latte", "Delicious latte", 250, 5.0, Map.of("Milk", "100 ml"));

        when(coffeeService.coffeeInfo("Latte")).thenReturn(coffeeInfo);

        mockMvc.perform(get("/api/v1/coffee?name=Latte")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Latte")))
                .andExpect(jsonPath("$.description", is("Delicious latte")))
                .andExpect(jsonPath("$.volume", is(250.0)))
                .andExpect(jsonPath("$.price", is(5.0)))
                .andExpect(jsonPath("$.ingredients.Milk", is("100 ml")));

        verify(coffeeService, times(1)).coffeeInfo("Latte");
    }

    @Test
    void testGetCoffeeInfoNotFound() throws Exception {
        when(coffeeService.coffeeInfo(anyString())).thenThrow(new NoSuchElementException("Coffee not found"));

        mockMvc.perform(get("/api/v1/coffee?name=Latte")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Resource not found")))
                .andExpect(jsonPath("$.ex", is("java.util.NoSuchElementException")))
                .andExpect(jsonPath("$.exceptionMessage", is("Coffee not found")));

        verify(coffeeService, times(1)).coffeeInfo("Latte");
    }


    @Test
    void testMakeCoffeeSuccess() throws Exception {
        OrderDto orderDto = new OrderDto();
        orderDto.setCoffeeName("Latte");
        orderDto.setPortionCoefficient(2.0);

        mockMvc.perform(post("/api/v1/coffee/make")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order is processed and sent to the queue"));

        verify(coffeeService, times(1))
                .sendToQueue(
                        argThat(
                                argument -> "Latte".equals(argument.getCoffeeName()) &&
                                2.0 == argument.getPortionCoefficient()
                        )
                );
    }


    @Test
    void testMakeCoffeeBadRequest() throws Exception {

        OrderDto orderDto = new OrderDto();
        orderDto.setCoffeeName("Latte");
        orderDto.setPortionCoefficient(-1.0);

        doThrow(new IllegalArgumentException("Invalid portion coefficient")).when(coffeeService).sendToQueue(
                argThat(arg -> "Latte".equals(arg.getCoffeeName()) &&
                        -1.0 == arg.getPortionCoefficient()
                ));

        mockMvc.perform(post("/api/v1/coffee/make")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", is("Invalid request")))
                .andExpect(jsonPath("$.ex", is("java.lang.IllegalArgumentException")))
                .andExpect(jsonPath("$.exceptionMessage", is("Invalid portion coefficient")));

        verify(coffeeService, times(1)).sendToQueue(argThat(arg ->
                "Latte".equals(arg.getCoffeeName()) &&
                        -1.0 == arg.getPortionCoefficient()
        ));
    }

    @Test
    void testNewRecipeSuccess() throws Exception {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("Latte");
        recipeDto.setDescription("Delicious latte");
        recipeDto.setVolume(250);
        recipeDto.setPrice(5.0);
        recipeDto.setIngredients(Map.of("Milk", 100.0));

        mockMvc.perform(post("/api/v1/coffee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe was successfully saved"));

        verify(coffeeService, times(1)).saveRecipe(argThat(dto ->
                "Latte".equals(dto.getName()) &&
                        "Delicious latte".equals(dto.getDescription()) &&
                        250 == dto.getVolume() &&
                        5.0 == dto.getPrice() &&
                        Map.of("Milk", 100.0).equals(dto.getIngredients())
        ));
    }

    @Test
    void testNewRecipeBadRequest() throws Exception {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName("Latte");
        recipeDto.setDescription("Delicious latte");
        recipeDto.setVolume(250);
        recipeDto.setPrice(5.0);
        recipeDto.setIngredients(Map.of("NonExistentIngredient", 100.0));

        doThrow(new NoSuchElementException("Ingredient does not exist")).when(coffeeService).saveRecipe(argThat(dto ->
                "Latte".equals(dto.getName()) &&
                        "Delicious latte".equals(dto.getDescription()) &&
                        250 == dto.getVolume() &&
                        5.0 == dto.getPrice() &&
                        Map.of("NonExistentIngredient", 100.0).equals(dto.getIngredients())
        ));

        mockMvc.perform(post("/api/v1/coffee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Resource not found")))
                .andExpect(jsonPath("$.ex", is("java.util.NoSuchElementException")))
                .andExpect(jsonPath("$.exceptionMessage", is("Ingredient does not exist")));

        verify(coffeeService, times(1)).saveRecipe(argThat(dto ->
                "Latte".equals(dto.getName()) &&
                        "Delicious latte".equals(dto.getDescription()) &&
                        250 == dto.getVolume() &&
                        5.0 == dto.getPrice() &&
                        Map.of("NonExistentIngredient", 100.0).equals(dto.getIngredients())
        ));
    }


    @Test
    void testDeleteRecipeSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/coffee?name=Latte"))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe was successfully deleted"));

        verify(coffeeService, times(1)).deleteRecipe("Latte");
    }

    @Test
    void testDeleteRecipeNotFound() throws Exception {
        doThrow(new NoSuchElementException("Coffee not found")).when(coffeeService).deleteRecipe(anyString());

        mockMvc.perform(delete("/api/v1/coffee?name=Latte"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Resource not found")))
                .andExpect(jsonPath("$.ex", is("java.util.NoSuchElementException")))
                .andExpect(jsonPath("$.exceptionMessage", is("Coffee not found")));

        verify(coffeeService, times(1)).deleteRecipe("Latte");
    }
}
