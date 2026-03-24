package com.smartiadev.auth_service.config;

import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdminIfNotExists() {

        String adminEmail = "admin@renthub.com";

        if (userRepository.existsByEmail(adminEmail)) {
            System.out.println("✅ Admin already exists");
            return;
        }

        User admin = User.builder()
                .username("admin")
                .email(adminEmail)
                .password(passwordEncoder.encode("Admin@123"))
                .fullName("RentHub Administrator")
                .city("System")
                .phone("0000000000")
                .roles(Set.of("ROLE_ADMIN"))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);

        System.out.println("🚀 Admin account created: admin@renthub.com / Admin@123");
    }
}
