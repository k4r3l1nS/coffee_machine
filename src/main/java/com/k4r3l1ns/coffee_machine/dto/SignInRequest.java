package com.k4r3l1ns.coffee_machine.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {

    private String username;

    private String password;

    public void throwIfInvalid() {
        if (
                username == null || password == null ||
                username.length() < 3 || username.length() > 24 || password.length() < 3 || password.length() > 255
        ) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
