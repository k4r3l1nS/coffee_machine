package com.k4r3l1ns.coffee_machine.controllers;

import com.k4r3l1ns.coffee_machine.dto.CoffeeInfo;
import com.k4r3l1ns.coffee_machine.dto.OrderDto;
import com.k4r3l1ns.coffee_machine.dto.RecipeDto;
import com.k4r3l1ns.coffee_machine.service.CoffeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coffee", description = "Coffee drink related API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${coffee.url}")
public class CoffeeController {

    private final CoffeeService coffeeService;

    @Operation(
            summary = "Получить информацию о кофе по названию кофе",
            description = "Возвращает данные кофе по названию кофе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(
                                    schema = @Schema(implementation = CoffeeInfo.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
                    @ApiResponse(responseCode = "404", description = "Кофе не найден")
            })
    @GetMapping("/info")
    public ResponseEntity<CoffeeInfo> getCoffeeInfo(
            @RequestParam(name = "name") String name
    ) {
        return ResponseEntity.ok(coffeeService.coffeeInfo(name));
    }

    @Operation(
            summary = "Сделать кофе",
            description = "Отправляет заказ в очередь сообщений Kafka",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Заказ содержит невалидные данные")
            })
    @PostMapping("/make")
    public ResponseEntity<String> makeCoffee(
            @RequestBody OrderDto orderDto
    ) {
        coffeeService.sendToQueue(orderDto);
        return ResponseEntity.ok("Order is processed and sent to the queue");
    }

    @Operation(
            summary = "Добавить новый рецепт кофе",
            description = "Сохраняет новый рецепт кофе, предварительно проверяя его",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Рецепт содержит невалидные данные"),
                    @ApiResponse(responseCode = "404", description = "Рецепт содержит несуществующий ингредиент")
            })
    @PostMapping
    public ResponseEntity<String> newRecipe(
            @RequestBody RecipeDto recipeDto
    ) {
        coffeeService.saveRecipe(recipeDto);
        return ResponseEntity.ok("Recipe was successfully saved");
    }

    @Operation(
            summary = "Удалить рецепт кофе",
            description = "Удаляет рецепт кофе, предварительно проверяя его наличие",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
                    @ApiResponse(responseCode = "404", description = "Название кофе не существует в БД")
            })
    @DeleteMapping
    public ResponseEntity<String> deleteRecipe(
            @RequestParam(name = "name") String name
    ) {
        coffeeService.deleteRecipe(name);
        return ResponseEntity.ok("Recipe was successfully deleted");
    }
}
