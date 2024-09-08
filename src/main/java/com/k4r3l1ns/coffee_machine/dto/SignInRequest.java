package com.k4r3l1ns.coffee_machine.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {

    @Size(min = 3, max = 24, message = "Username must consist of 3-24 characters")
    private String username;

    @Size(max = 255, message = "Username must consist of no more than 255 characters")
    private String password;
}
