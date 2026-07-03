package com.example.demo.User;

import com.example.demo.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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


            if ((userRepository.existsByEmail(user.getEmail()))) {
                throw new RuntimeException("Bu e-posta adresi zaten kullanımda: " + user.getEmail());
            }
            if (user.getRole() == null) {
                user.setRole(Role.CUSTOMER);
            }
            user.setIsActive(true);
            user = userRepository.save(user);

            UserResponseDto response = new UserResponseDto();
            response.setId(user.getId());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setEmail(user.getEmail());

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
        foundUser=userRepository.save(foundUser);

        UserResponseDto response = new UserResponseDto();
        response.setId(foundUser.getId());
        response.setFirstName(foundUser.getFirstName());
        response.setLastName(foundUser.getLastName());
        response.setEmail(foundUser.getEmail());

        return response;
    }

}
