package com.example.demo.User;

import com.example.demo.enums.Role;
import lombok.Data;

@Data
public class UpdateUserRoleDto {
    private Long userId;
    private Role role;
}
