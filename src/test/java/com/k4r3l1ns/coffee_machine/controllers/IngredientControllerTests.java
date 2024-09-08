package com.k4r3l1ns.coffee_machine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k4r3l1ns.coffee_machine.config.NoSecurityConfig;
import com.k4r3l1ns.coffee_machine.dto.IngredientDto;
import com.k4r3l1ns.coffee_machine.handler.CustomExceptionHandler;
import com.k4r3l1ns.coffee_machine.service.IngredientService;
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

import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngredientController.class)
@Import({CustomExceptionHandler.class, NoSecurityConfig.class})
public class IngredientControllerTests {

    @MockBean
    private IngredientService ingredientService;

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
    void testAddIngredientSuccess() throws Exception {

        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Milk");
        ingredientDto.setMeasurement("ml");

        mockMvc.perform(post("/api/v1/ingredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredientDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Ingredient successfully added"));

        verify(ingredientService, times(1)).save(argThat(dto ->
                "Milk".equals(dto.getName()) && "ml".equals(dto.getMeasurement())
        ));
    }


    @Test
    void testAddIngredientBadRequest() throws Exception {
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("");
        ingredientDto.setMeasurement("ml");

        doThrow(new IllegalArgumentException("Invalid name"))
                .when(ingredientService).save(argThat(dto ->
                        "".equals(dto.getName()) &&
                                "ml".equals(dto.getMeasurement())
                ));

        mockMvc.perform(post("/api/v1/ingredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ingredientDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", is("Invalid request")))
                .andExpect(jsonPath("$.ex", is("java.lang.IllegalArgumentException")))
                .andExpect(jsonPath("$.exceptionMessage", is("Invalid name")));

        verify(ingredientService, times(1)).save(argThat(dto ->
                "".equals(dto.getName()) &&
                        "ml".equals(dto.getMeasurement())
        ));
    }


    @Test
    void testDeleteIngredientSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/ingredient?name=Milk"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ingredient successfully deleted"));

        verify(ingredientService, times(1)).delete("Milk");
    }

    @Test
    void testDeleteIngredientNotFound() throws Exception {
        doThrow(new NoSuchElementException("Ingredient not found")).when(ingredientService).delete(anyString());

        mockMvc.perform(delete("/api/v1/ingredient?name=Milk"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Resource not found")))
                .andExpect(jsonPath("$.ex", is("java.util.NoSuchElementException")))
                .andExpect(jsonPath("$.exceptionMessage", is("Ingredient not found")));

        verify(ingredientService, times(1)).delete("Milk");
    }

    @Test
    void testIncreaseResidualsSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/ingredient/residual/increase")
                        .param("ingredientName", "Milk")
                        .param("incrementedAmount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Residual amount successfully changed"));

        verify(ingredientService, times(1)).increaseResiduals("Milk", 50.0);
    }

    @Test
    void testGetResidualsSuccess() throws Exception {
        when(ingredientService.getResiduals("Milk")).thenReturn(100.0 + " " + "ML");

        mockMvc.perform(get("/api/v1/ingredient/residual")
                        .param("ingredientName", "Milk"))
                .andExpect(status().isOk())
                .andExpect(content().string("Residual for Milk: 100.0 ML"));

        verify(ingredientService, times(1)).getResiduals("Milk");
    }

    @Test
    void testGetResidualsNotFound() throws Exception {
        when(ingredientService.getResiduals(anyString())).thenThrow(new NoSuchElementException("Ingredient not found"));

        mockMvc.perform(get("/api/v1/ingredient/residual")
                        .param("ingredientName", "Milk"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Resource not found")))
                .andExpect(jsonPath("$.ex", is("java.util.NoSuchElementException")))
                .andExpect(jsonPath("$.exceptionMessage", is("Ingredient not found")));

        verify(ingredientService, times(1)).getResiduals("Milk");
    }
}
