package com.k4r3l1ns.coffee_machine.controllers;

import com.k4r3l1ns.coffee_machine.dto.JwtAuthenticationResponse;
import com.k4r3l1ns.coffee_machine.dto.SignInRequest;
import com.k4r3l1ns.coffee_machine.dto.SignUpRequest;
import com.k4r3l1ns.coffee_machine.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "API providing sign in and sign up operations")
@RestController
@RequestMapping(path = "${auth.url}", method = RequestMethod.POST)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Зарегистрироваться",
            description = "Зарегистрировать пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(
                                    schema = @Schema(implementation = JwtAuthenticationResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Данные невалидны")
            })
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(
            @RequestBody @Valid SignUpRequest request
    ) {
        return authenticationService.signUp(request);
    }

    @Operation(
            summary = "Войти",
            description = "Войти в систему",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешный ответ",
                            content = @Content(
                                    schema = @Schema(implementation = JwtAuthenticationResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Данные невалидны")
            })
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(
            @RequestBody @Valid SignInRequest request
    ) {
        return authenticationService.signIn(request);
    }
}
