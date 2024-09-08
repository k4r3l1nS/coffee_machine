package com.k4r3l1ns.coffee_machine.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.regex.Pattern;

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

    public void throwIfInvalid() {
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        if (
                username == null || password == null || email == null ||
                username.length() < 3 || username.length() > 24 || password.length() < 3 || password.length() > 255 ||
                !emailPattern.matcher(email).matches()
        ) {
            throw new IllegalArgumentException("Invalid request data");
        }
    }
}