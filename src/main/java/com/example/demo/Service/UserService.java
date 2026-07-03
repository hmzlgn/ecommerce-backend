package com.example.demo.Service;

import com.example.demo.DTO.UserLoginDto;
import com.example.demo.DTO.UserRegisterDto;
import com.example.demo.DTO.UserResponseDto;
import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

        public UserResponseDto registerUser(UserRegisterDto request) {

            User newUser = new User();
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(request.getPassword());


            if ((userRepository.existsByEmail(newUser.getEmail()))) {
                throw new RuntimeException("Bu e-posta adresi zaten kullanımda: " + newUser.getEmail());
            }
            if (newUser.getRole() == null) {
                newUser.setRole(Role.CUSTOMER);
            }
            newUser.setIsActive(true);
            User savedUser = userRepository.save(newUser);

            UserResponseDto response = new UserResponseDto();
            response.setId(savedUser.getId());
            response.setFirstName(savedUser.getFirstName());
            response.setLastName(savedUser.getLastName());
            response.setEmail(savedUser.getEmail());

            return response;
        }

    public UserResponseDto login(UserLoginDto request) { //dışardan gelen tepsi request. dışarı giden response

        User foundUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("E posta veya şifre hatalı!"));
        if (!foundUser.getIsActive()) {
            throw new RuntimeException("Hesabınız aktif değildir. Lütfen destek ile iletişime geçiniz.");
        }
        if (!foundUser.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("E-posta veya şifre hatalı!");
        }
        foundUser.setLastLoginAt(LocalDateTime.now());
        userRepository.save(foundUser);

        UserResponseDto response = new UserResponseDto();
        response.setId(foundUser.getId());
        response.setFirstName(foundUser.getFirstName());
        response.setLastName(foundUser.getLastName());
        response.setEmail(foundUser.getEmail());

        return response;
    }

}
