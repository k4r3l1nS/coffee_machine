package com.k4r3l1ns.coffee_machine.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @Size(min = 3, max = 24, message = "Username must consist of 3-24 characters")
    private String username;

    @Email(message = "Email is formatted as user@example.com")
    private String email;

    @Size(max = 255, message = "Username must consist of no more than 255 characters")
    private String password;
}