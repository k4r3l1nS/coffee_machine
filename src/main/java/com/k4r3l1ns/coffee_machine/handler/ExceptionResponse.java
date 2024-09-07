package com.k4r3l1ns.coffee_machine.handler;

import org.springframework.http.HttpStatus;

public record ExceptionResponse(
        HttpStatus status,
        int code,
        String message,
        Class<? extends Exception> ex,
        String exceptionMessage
) {}
