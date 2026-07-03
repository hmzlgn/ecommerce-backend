package com.example.demo.User;

import com.example.demo.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto registerUser(UserRegisterDto request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda: " + user.getEmail());
        }
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        user.setIsActive(true);
        user = userRepository.save(user);

        return mapToResponse(user);
    }

    public UserResponseDto login(UserLoginDto request) {
        User foundUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("E-posta veya şifre hatalı!"));

        if (!foundUser.getIsActive()) {
            throw new RuntimeException("Hesabınız aktif değildir. Lütfen destek ile iletişime geçiniz.");
        }
        if (!foundUser.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("E-posta veya şifre hatalı!");
        }

        foundUser.setLastLoginAt(LocalDateTime.now());
        foundUser = userRepository.save(foundUser);

        return mapToResponse(foundUser);
    }

    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı! Kullanıcı ID: " + id));

        return mapToResponse(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDto updateUser(Long id, UserUpdateDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı! Kullanıcı ID: " + id));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı! Kullanıcı ID: " + id));

        user.setIsActive(false);
        userRepository.save(user);
    }

    public void changePassword(Long id, ChangePasswordDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı! Kullanıcı ID: " + id));

        if (!user.getPassword().equals(request.getOldPasswword())) {
            throw new RuntimeException("Mevcut şifre hatalı!");
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Yeni şifreler birbiriyle eşleşmiyor!");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }

    private UserResponseDto mapToResponse(User user) {
        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        return response;
    }
}