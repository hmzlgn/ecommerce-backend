package com.example.demo.User;

import lombok.Data;
import lombok.Getter;

@Data
public class ChangePasswordDto {
    private String oldPasswword;
    private String newPassword;
    private String confirmNewPassword;
}
