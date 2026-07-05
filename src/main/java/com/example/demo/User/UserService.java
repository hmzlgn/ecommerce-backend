package com.example.demo.User;

import com.example.demo.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // --- 1. KİMLİK VE GÜVENLİK İŞLEMLERİ (AUTH & SECURITY) ---

    public UserResponseDto registerUser(UserRegisterDto request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda: " + user.getEmail());
        }

        user.setRole(Role.CUSTOMER);
        user.setIsActive(false); // GERÇEK SİSTEM STANDARDI: Mail doğrulanana kadar hesap pasiftir.
        user = userRepository.save(user);

        // TODO: İleride MailService eklendiğinde burada kullanıcıya doğrulama maili atılacak.

        return mapToResponse(user);
    }

    public UserResponseDto login(UserLoginDto request) {
        User foundUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("E-posta veya şifre hatalı!"));

        if (!foundUser.getIsActive()) {
            throw new RuntimeException("Hesabınız henüz onaylanmamış veya askıya alınmış.");
        }
        if (!foundUser.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("E-posta veya şifre hatalı!");
        }

        // TODO: Spring Security / JWT eklendiğinde burada geriye sadece UserResponseDto değil, bir JWT Token da dönülecek.

        foundUser.setLastLoginAt(LocalDateTime.now());
        foundUser = userRepository.save(foundUser);

        return mapToResponse(foundUser);
    }

    public void verifyEmail(String token) {
        // TODO: İleride Token tablosundan bu token'ı bulup, ilişkili User'ın isActive durumunu true yapacağız.
    }

    public void forgotPassword(ForgotPasswordDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Bu e-posta adresine ait kullanıcı bulunamadı."));

        // TODO: Rastgele UUID (Token) üretilecek.
        // TODO: Üretilen Token veritabanına kaydedilecek.
        // TODO: MailService ile kullanıcıya şifre sıfırlama linki atılacak.
    }

    public void resetPassword(ResetPasswordDto request) {
        // TODO: DTO içinden gelen token veritabanında doğrulanacak.
        // TODO: Süresi geçmemişse kullanıcının şifresi DTO'dan gelen yeni şifre ile değiştirilecek.
    }

    public void changePassword(Long id, ChangePasswordDto request) {
        // TODO: Spring Security eklendiğinde URL'den gelen ID ile sisteme giriş yapmış (JWT) kişinin ID'si aynı mı diye kontrol edilecek.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı!"));

        if (!user.getPassword().equals(request.getOldPasswword())) {
            throw new RuntimeException("Mevcut şifre hatalı!");
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Yeni şifreler birbiriyle eşleşmiyor!");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }

    // --- 2. PROFİL YÖNETİMİ (PROFILE MANAGEMENT) ---

    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı!"));

        return mapToResponse(user);
    }

    public UserResponseDto updateUser(Long id, UserUpdateDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı!"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    // --- 3. YÖNETİCİ OPERASYONLARI (ADMIN OPERATIONS) ---

    public Page<UserResponseDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(this::mapToResponse);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı!"));

        user.setIsActive(false);
        userRepository.save(user);
    }

    public UserResponseDto updateUserRole(Long id, UpdateUserRoleDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı!"));

        user.setRole(request.getRole());
        user = userRepository.save(user);

        return mapToResponse(user);
    }

    // --- 4. YARDIMCI METOTLAR (HELPERS) ---

    private UserResponseDto mapToResponse(User user) {
        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole()); // Frontend'in rolü bilmesi admin paneli çizimi için önemlidir.
        return response;
    }
}
