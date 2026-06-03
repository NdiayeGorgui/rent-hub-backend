package com.smartiadev.auth_service.kafka;

import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.repository.UserRepository;
import com.smartiadev.base_domain_service.dto.RentalCancelledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalCancelledConsumer {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @KafkaListener(
            topics = "rental.cancelled",
            groupId = "auth-service"
    )
    public void cancelled(RentalCancelledEvent event) {

        try {

            User renter = userRepository
                    .findById(event.renterId())
                    .orElse(null);

            if (renter == null) {
                return;
            }

            sendCancelledEmail(
                    renter,
                    event.itemTitle()
            );

        } catch (Exception e) {
            log.error("Erreur email rental.cancelled", e);
        }
    }

    private void sendCancelledEmail(
            User renter,
            String itemTitle
    ) {

        SimpleMailMessage mail = new SimpleMailMessage();
        String rentalLink =
                "https://app.gonifty.ca";

        mail.setFrom("team.smartiadev@gmail.com");
        mail.setTo(renter.getEmail());

        mail.setSubject("Demande de location annulée ❌");

        mail.setText(
                "Bonjour " + renter.getFullName() + ",\n\n" +

                        "Votre demande de location pour l'item suivant n'a pas été retenue :\n\n" +

                        "📦 " + itemTitle + "\n\n" +

                        "Cet item a été attribué à un autre locataire.\n\n" +

                        "Vous pouvez consulter d'autres annonces disponibles sur Gonifty :\n\n" +

                        rentalLink + "\n\n" +

                        "Merci de votre confiance,\n" +
                        "L'équipe Gonifty"
        );

        mailSender.send(mail);
    }
}