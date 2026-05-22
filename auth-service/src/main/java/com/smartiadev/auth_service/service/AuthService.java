package com.smartiadev.auth_service.service;

import com.smartiadev.auth_service.dto.*;
import com.smartiadev.auth_service.dto.request.LoginRequest;
import com.smartiadev.auth_service.dto.request.RegisterRequest;
import com.smartiadev.auth_service.dto.response.AuthResponse;
import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.kafka.AuctionEventPublisher;
import com.smartiadev.auth_service.repository.UserRepository;
import com.smartiadev.auth_service.security.JwtService;
import com.smartiadev.base_domain_service.dto.AuctionStrikeEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuctionEventPublisher auctionEventPublisher;
    private final JavaMailSender mailSender;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .phone(request.phone())
                .city(request.city())
                .createdAt(LocalDateTime.now())
                .roles(Set.of("ROLE_USER"))
                .enabled(true)
                .build();

        userRepository.save(user);

        // ← Envoie le mail de bienvenue
        sendWelcomeEmail(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    private void sendWelcomeEmail(User user) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom("team.smartiadev@gmail.com");
            mail.setTo(user.getEmail());
            mail.setSubject("Bienvenue sur Gonifty 🎉");
            mail.setText(
                    "Bonjour " + user.getFullName() + ",\n\n" +
                            "Bienvenue sur Gonifty ! 🎉\n\n" +
                            "Votre compte a été créé avec succès.\n\n" +
                            "📧 Email : " + user.getEmail() + "\n" +
                            "👤 Pseudo : @" + user.getUsername() + "\n\n" +
                            "Vous pouvez dès maintenant :\n" +
                            "• Publier des items à louer\n" +
                            "• Parcourir les annonces\n" +
                            "• Passer Premium pour participer aux enchères\n\n" +
                            "Accédez à l'application : https://app.gonifty.ca\n\n" +
                            "À bientôt sur Gonifty !\n" +
                            "L'équipe Gonifty"
            );
            mailSender.send(mail);
        } catch (Exception e) {
            // Ne pas bloquer l'inscription si le mail échoue
            log.warn("Impossible d'envoyer le mail de bienvenue à {}", user.getEmail());
        }
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Email ou mot de passe incorrect"
                ));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Email ou mot de passe incorrect"
            );
        }

        if (!user.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Votre compte a été suspendu"
            );
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public UserResponse getUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getUsername(),
                user.getRoles()
        );
    }


    public UserBidEligibilityResponse checkBidEligibility(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean canBid =
                user.isEnabled()
                        && !user.isAuctionRestricted();

        return new UserBidEligibilityResponse(
                canBid,
                user.isEnabled(),
                user.isAuctionRestricted(),
                user.getAuctionStrikes()
        );
    }

    @Transactional
    public AuctionStrikeResponse addAuctionStrike(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int strikes = user.getAuctionStrikes() + 1;

        user.setAuctionStrikes(strikes);

        if (strikes >= 3) {
            user.setAuctionRestricted(true);
        }

        userRepository.save(user);
        auctionEventPublisher.publishAuctionStrike(
                new AuctionStrikeEvent(
                        user.getId(),
                        user.getAuctionStrikes(),
                        user.isAuctionRestricted()
                )
        );

        return AuctionStrikeResponse.builder()
                .auctionStrikes(strikes)
                .auctionRestricted(user.isAuctionRestricted())
                .build();
    }

    public List<UserResponse> getAdmins() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRoles() != null
                        && user.getRoles().contains("ROLE_ADMIN"))
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getUsername(),
                        user.getRoles()
                ))
                .toList();
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        // On ne révèle pas si l'email existe ou non (sécurité)
        User user = userRepository.findByEmail(request.email())
                .orElse(null);

        if (user == null) return;

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Envoie le mail
        String resetLink = "https://app.gonifty.ca/reset-password?token=" + token;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("team.smartiadev@gmail.com");  // ← déjà fait ✅
        mail.setTo(user.getEmail());
        mail.setSubject("Réinitialisation de votre mot de passe - Gonifty");
        mail.setText(
                "Bonjour " + user.getFullName() + ",\n\n" +
                        "Vous avez demandé une réinitialisation de mot de passe.\n\n" +
                        "Cliquez sur ce lien pour réinitialiser votre mot de passe :\n" +
                        resetLink + "\n\n" +
                        "Ce lien expire dans 15 minutes.\n\n" +
                        "Si vous n'avez pas fait cette demande, ignorez cet email.\n\n" +
                        "L'équipe Gonifty"
        );
        mailSender.send(mail);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByResetToken(request.token())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Token invalide ou expiré"
                ));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token expiré"
            );
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}