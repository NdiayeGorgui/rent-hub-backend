package com.smartiadev.auth_service.kafka;

import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.repository.UserRepository;
import com.smartiadev.base_domain_service.dto.RentalApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalApprovedConsumer {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @KafkaListener(
            topics = "rental.approved",
            groupId = "auth-service"
    )
    public void approved(RentalApprovedEvent event) {

        try {

            User renter = userRepository
                    .findById(event.renterId())
                    .orElse(null);

            if (renter == null) {
                return;
            }

            sendApprovedEmail(
                    renter,
                    event.itemTitle()
            );

        } catch (Exception e) {
            log.error("Erreur email rental.approved", e);
        }
    }

    private void sendApprovedEmail(
            User renter,
            String itemTitle
    ) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setFrom("team.smartiadev@gmail.com");
        mail.setTo(renter.getEmail());

        mail.setSubject("Location approuvée ✅");

        mail.setText(
                "Bonjour " + renter.getFullName() + ",\n\n" +

                        "Bonne nouvelle ! 🎉\n\n" +

                        "Votre demande de location a été approuvée pour l'item suivant :\n\n" +

                        "📦 " + itemTitle + "\n\n" +

                        "Le propriétaire a accepté votre demande.\n\n" +

                        "Vous pouvez consulter les détails de votre location sur Gonifty :\n\n" +

                        "https://app.gonifty.ca/rentals\n\n" +

                        "Merci de votre confiance,\n" +
                        "L'équipe Gonifty"
        );

        mailSender.send(mail);
    }
}
