package com.k4r3l1ns.coffee_machine.controllers;

import com.k4r3l1ns.coffee_machine.dto.StatisticsDto;
import com.k4r3l1ns.coffee_machine.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${statistics.url}", method = RequestMethod.GET)
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/most-ordered-coffee")
    public ResponseEntity<List<StatisticsDto>> getMostOrderedCoffee(
            @RequestParam(defaultValue = "1") int count
    ) {
        return ResponseEntity.ok(statisticsService.mostOrderedCoffee(count));
    }

    @GetMapping("/most-profitable-coffee")
    public ResponseEntity<List<StatisticsDto>> getProfitableCoffee(
            @RequestParam(defaultValue = "1") int count
    ) {
        return ResponseEntity.ok(statisticsService.mostProfitableCoffee(count));
    }

    @GetMapping("/most-served-coffee")
    public ResponseEntity<List<StatisticsDto>> getMostServedCoffee(
            @RequestParam(defaultValue = "1") int count
    ) {
        return ResponseEntity.ok(statisticsService.mostServedCoffee(count));
    }
}
