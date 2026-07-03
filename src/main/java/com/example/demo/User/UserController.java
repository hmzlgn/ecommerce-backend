package com.example.demo.User;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. Yeni Kullanıcı Kaydı (Kayıt Ol)
    @PostMapping("/register")
    public UserResponseDto registerUser(@RequestBody UserRegisterDto request) {
        return userService.registerUser(request);
    }

    // 2. Kullanıcı Girişi (Login)
    @PostMapping("/login")
    public UserResponseDto login(@RequestBody UserLoginDto request) {
        return userService.login(request);
    }

    // 3. Tüm Kullanıcıları Listeleme
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    // 4. Belirli Bir Kullanıcıyı Getirme
    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // 5. Kullanıcı Bilgilerini Güncelleme (İsim, Soyisim vb.)
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto request) {
        return userService.updateUser(id, request);
    }

    // 6. Şifre Değiştirme
    @PutMapping("/{id}/password")
    public void changePassword(@PathVariable Long id, @RequestBody ChangePasswordDto request) {
        userService.changePassword(id, request);
    }

    // 7. Kullanıcıyı Pasife Alma (Soft Delete)
    @DeleteMapping("/{id}")
    public void deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
    }
}