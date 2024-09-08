package com.k4r3l1ns.coffee_machine.service;

import com.k4r3l1ns.coffee_machine.dao.StatisticsRepository;
import com.k4r3l1ns.coffee_machine.dto.StatisticsDto;
import com.k4r3l1ns.coffee_machine.mappers.StatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final StatisticsMapper statisticsMapper;

    public List<StatisticsDto> mostOrderedCoffee(int count) {
        return statisticsRepository.mostOrderedCoffee(count).stream().map(
                statisticsMapper::toDto
        ).toList();
    }

    public List<StatisticsDto> mostProfitableCoffee(int count) {
        return statisticsRepository.mostProfitableCoffee(count).stream().map(
                statisticsMapper::toDto
        ).toList();
    }

    public List<StatisticsDto> mostServedCoffee(int count) {
        return statisticsRepository.mostServedCoffee(count).stream().map(
                statisticsMapper::toDto
        ).toList();
    }
}
