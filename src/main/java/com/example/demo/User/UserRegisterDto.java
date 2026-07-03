package com.example.demo.User;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
