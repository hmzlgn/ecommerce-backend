package com.example.demo.User;

import com.example.demo.enums.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
