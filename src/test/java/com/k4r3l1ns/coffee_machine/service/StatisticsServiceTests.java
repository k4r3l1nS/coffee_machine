package com.k4r3l1ns.coffee_machine.service;

import com.k4r3l1ns.coffee_machine.dao.StatisticsRepository;
import com.k4r3l1ns.coffee_machine.dto.StatisticsDto;
import com.k4r3l1ns.coffee_machine.mappers.StatisticsMapper;
import com.k4r3l1ns.coffee_machine.models.Statistics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTests {

    @Mock
    private StatisticsRepository statisticsRepository;

    @Mock
    private StatisticsMapper statisticsMapper;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void testMostOrderedCoffee() {

        int count = 2;
        var statisticsEntity = new Statistics();
        var statisticsDto = new StatisticsDto();
        when(statisticsRepository.mostOrderedCoffee(count)).thenReturn(List.of(statisticsEntity));
        when(statisticsMapper.toDto(statisticsEntity)).thenReturn(statisticsDto);

        List<StatisticsDto> result = statisticsService.mostOrderedCoffee(count);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(statisticsDto, result.get(0));

        verify(statisticsRepository, times(1)).mostOrderedCoffee(count);
        verify(statisticsMapper, times(1)).toDto(statisticsEntity);
    }

    @Test
    void testMostProfitableCoffee() {

        int count = 3;
        var statisticsEntity = new Statistics();
        var statisticsDto = new StatisticsDto();
        when(statisticsRepository.mostProfitableCoffee(count)).thenReturn(List.of(statisticsEntity));
        when(statisticsMapper.toDto(statisticsEntity)).thenReturn(statisticsDto);

        List<StatisticsDto> result = statisticsService.mostProfitableCoffee(count);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(statisticsDto, result.get(0));

        verify(statisticsRepository, times(1)).mostProfitableCoffee(count);
        verify(statisticsMapper, times(1)).toDto(statisticsEntity);
    }

    @Test
    void testMostServedCoffee() {

        int count = 2;
        var statisticsEntity = new Statistics();
        var statisticsDto = new StatisticsDto();
        when(statisticsRepository.mostServedCoffee(count)).thenReturn(List.of(statisticsEntity));
        when(statisticsMapper.toDto(statisticsEntity)).thenReturn(statisticsDto);

        List<StatisticsDto> result = statisticsService.mostServedCoffee(count);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(statisticsDto, result.get(0));

        verify(statisticsRepository, times(1)).mostServedCoffee(count);
        verify(statisticsMapper, times(1)).toDto(statisticsEntity);
    }
}
