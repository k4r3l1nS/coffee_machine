package com.k4r3l1ns.coffee_machine.controllers;

import com.k4r3l1ns.coffee_machine.dto.StatisticsDto;
import com.k4r3l1ns.coffee_machine.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Statistics", description = "Coffee drink statistics related API")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${statistics.url}", method = RequestMethod.GET)
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(
            summary = "Получить наиболее часто заказываемый напиток",
            description = "Возвращает наиболее часто заказываемый напиток",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(schema = @Schema(implementation = List.class))
                    )
            })
    @GetMapping("/most-ordered-coffee")
    public ResponseEntity<List<StatisticsDto>> getMostOrderedCoffee(
            @RequestParam(name = "count", defaultValue = "1") int count
    ) {
        return ResponseEntity.ok(statisticsService.mostOrderedCoffee(count));
    }

    @Operation(
            summary = "Получить наиболее окупаемый напиток",
            description = "Возвращает напиток с наибольшей выручкой",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(schema = @Schema(implementation = List.class))
                    )
            })
    @GetMapping("/most-profitable-coffee")
    public ResponseEntity<List<StatisticsDto>> getProfitableCoffee(
            @RequestParam(name = "count", defaultValue = "1") int count
    ) {
        return ResponseEntity.ok(statisticsService.mostProfitableCoffee(count));
    }

    @Operation(
            summary = "Получить напиток с наибольшим количеством заказанных порций",
            description = "Возвращает напиток с наибольшим количеством заказанных порций",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(schema = @Schema(implementation = List.class))
                    )
            })
    @GetMapping("/most-served-coffee")
    public ResponseEntity<List<StatisticsDto>> getMostServedCoffee(
            @RequestParam(name = "count", defaultValue = "1") int count
    ) {
        return ResponseEntity.ok(statisticsService.mostServedCoffee(count));
    }
}
