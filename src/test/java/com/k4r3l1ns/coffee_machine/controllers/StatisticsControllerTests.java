package com.k4r3l1ns.coffee_machine.controllers;

import com.k4r3l1ns.coffee_machine.dto.StatisticsDto;
import com.k4r3l1ns.coffee_machine.handler.CustomExceptionHandler;
import com.k4r3l1ns.coffee_machine.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@Import(CustomExceptionHandler.class)
public class StatisticsControllerTests {

    @MockBean
    private StatisticsService statisticsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetMostOrderedCoffeeSuccess() throws Exception {

        StatisticsDto dto = new StatisticsDto("Latte", 0, 50, 0);
        List<StatisticsDto> statistics = List.of(dto);

        when(statisticsService.mostOrderedCoffee(anyInt())).thenReturn(statistics);

        mockMvc.perform(get("/api/v1/stats/most-ordered-coffee")
                        .param("count", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Latte")))
                .andExpect(jsonPath("$[0].overallRevenue", is(0.0)))
                .andExpect(jsonPath("$[0].orderCount", is(50)))
                .andExpect(jsonPath("$[0].portionCount", is(0.0)))
                .andExpect(jsonPath("$.length()",is(1)));

        verify(statisticsService, times(1)).mostOrderedCoffee(2);
    }

    @Test
    void testGetMostOrderedCoffeeNotFound() throws Exception {

        when(statisticsService.mostOrderedCoffee(anyInt())).thenThrow(new NoSuchElementException("No data available"));

        mockMvc.perform(get("/api/v1/stats/most-ordered-coffee")
                        .param("count", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Resource not found")))
                .andExpect(jsonPath("$.ex", is("java.util.NoSuchElementException")))
                .andExpect(jsonPath("$.exceptionMessage", is("No data available")));

        verify(statisticsService, times(1)).mostOrderedCoffee(2);
    }

    @Test
    void testGetProfitableCoffeeSuccess() throws Exception {

        StatisticsDto statisticsDto = new StatisticsDto("Latte", 2000, 0, 0);
        List<StatisticsDto> statistics = List.of(statisticsDto);

        when(statisticsService.mostProfitableCoffee(anyInt())).thenReturn(statistics);

        mockMvc.perform(get("/api/v1/stats/most-profitable-coffee")
                        .param("count", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Latte")))
                .andExpect(jsonPath("$[0].overallRevenue", is(2000.0)))
                .andExpect(jsonPath("$[0].orderCount", is(0)))
                .andExpect(jsonPath("$[0].portionCount", is(0.0)))
                .andExpect(jsonPath("$.length()",is(1)));

        verify(statisticsService, times(1)).mostProfitableCoffee(2);
    }

    @Test
    void testGetProfitableCoffeeNotFound() throws Exception {

        when(statisticsService.mostProfitableCoffee(anyInt())).thenThrow(new NoSuchElementException("No data available"));

        mockMvc.perform(get("/api/v1/stats/most-profitable-coffee")
                        .param("count", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Resource not found")))
                .andExpect(jsonPath("$.ex", is("java.util.NoSuchElementException")))
                .andExpect(jsonPath("$.exceptionMessage", is("No data available")));

        verify(statisticsService, times(1)).mostProfitableCoffee(2);
    }

    @Test
    void testGetMostServedCoffeeSuccess() throws Exception {

        StatisticsDto dto = new StatisticsDto("Latte", 0, 0, 300);
        List<StatisticsDto> statistics = List.of(dto);

        when(statisticsService.mostServedCoffee(anyInt())).thenReturn(statistics);

        mockMvc.perform(get("/api/v1/stats/most-served-coffee")
                        .param("count", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("Latte")))
                .andExpect(jsonPath("$[0].overallRevenue", is(0.0)))
                .andExpect(jsonPath("$[0].orderCount", is(0)))
                .andExpect(jsonPath("$[0].portionCount", is(300.0)))
                .andExpect(jsonPath("$.length()",is(1)));

        verify(statisticsService, times(1)).mostServedCoffee(2);
    }

    @Test
    void testGetMostServedCoffeeNotFound() throws Exception {
        when(statisticsService.mostServedCoffee(anyInt())).thenThrow(new NoSuchElementException("No data available"));

        mockMvc.perform(get("/api/v1/stats/most-served-coffee")
                        .param("count", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", is("Resource not found")))
                .andExpect(jsonPath("$.ex", is("java.util.NoSuchElementException")))
                .andExpect(jsonPath("$.exceptionMessage", is("No data available")));

        verify(statisticsService, times(1)).mostServedCoffee(2);
    }
}
