package com.k4r3l1ns.coffee_machine.controllers;

import com.k4r3l1ns.coffee_machine.dto.IngredientDto;
import com.k4r3l1ns.coffee_machine.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Ingredient", description = "Ingredient related API")
@RestController
@RequiredArgsConstructor
@RequestMapping("${ingredient.url}")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(
            summary = "Добавить новый ингредиент",
            description = "Сохраняет новый ингредиент, предварительно проверяя его",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Ингредиент содержит невалидные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ингредиент имеет несуществующую единицу измерения"
                    )
            })
    @PostMapping
    public ResponseEntity<String> addIngredient(
            @RequestBody IngredientDto ingredientDto
    ) {
        ingredientService.save(ingredientDto);
        return ResponseEntity.ok("Ingredient successfully added");
    }

    @Operation(
            summary = "Удалить ингредиент",
            description = "Удаляет ингредиент, предварительно проверяя его наличие",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ингредиент используется в рецептах и не может быть удалён"
                    ),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ингредиент не найден"
                    )
            })
    @DeleteMapping
    public ResponseEntity<String> deleteIngredient(
            @RequestParam(name = "name") String name
    ) {
        ingredientService.delete(name);
        return ResponseEntity.ok("Ingredient successfully deleted");
    }

    @Operation(
            summary = "Изменить количество ингредиента",
            description = "Изменяет количество остатка ингредиента, проверяя его наличие",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(
                                    schema = @Schema(implementation = String.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Остаток ингредиента становится меньше 0"
                    ),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ингредиент не найден"
                    )
            })
    @PostMapping("/residual/increase")
    public ResponseEntity<String> increaseResiduals(
            @RequestParam(name = "ingredientName") String ingredientName,
            @RequestParam(name = "incrementedAmount") double incrementedAmount
    ) {
        ingredientService.increaseResiduals(
                ingredientName,
                incrementedAmount
        );
        return ResponseEntity.ok("Residual amount successfully changed");
    }

    @Operation(
            summary = "Получить количество ингредиента",
            description = "Возвращает количество остатка ингредиента, проверяя его наличие",
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
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ингредиент не найден"
                    )
            })
    @GetMapping("/residual")
    public ResponseEntity<String> getResiduals(
            @RequestParam(name = "ingredientName") String ingredientName
    ) {
        return ResponseEntity.ok(
                "Residual for " + ingredientName + ": " + ingredientService.getResiduals(ingredientName)
        );
    }
}
