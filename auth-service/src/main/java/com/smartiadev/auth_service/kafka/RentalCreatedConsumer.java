package com.smartiadev.auth_service.kafka;

import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.repository.UserRepository;
import com.smartiadev.base_domain_service.dto.RentalCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalCreatedConsumer {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @KafkaListener(
            topics = "rental.created",
            groupId = "auth-service"
    )
    public void consume(RentalCreatedEvent event) {

        try {

            User owner = userRepository.findById(event.ownerId())
                    .orElse(null);

            if (owner == null) {
                return;
            }

            sendRentalRequestEmail(owner, event);

        } catch (Exception e) {
            log.error("Erreur lors du traitement de rental-created", e);
        }
    }

    private void sendRentalRequestEmail(
            User owner,
            RentalCreatedEvent event
    ) {

        SimpleMailMessage mail = new SimpleMailMessage();
        String rentalLink =
                "https://app.gonifty.ca/rentals";

        mail.setFrom("team.smartiadev@gmail.com");
        mail.setTo(owner.getEmail());

        mail.setSubject("Nouvelle demande de location ⏳");

        mail.setText(
                "Bonjour " + owner.getFullName() + ",\n\n" +

                        "Vous avez reçu une nouvelle demande pour :\n\n" +

                        "📦 " + event.itemTitle() + "\n\n" +

                        "👤 Locataire : @" + event.renterUsername() + "\n" +
                        "📅 Du : " + event.startDate() + "\n" +
                        "📅 Au : " + event.endDate() + "\n\n" +

                        "Cette demande est en attente de votre approbation.\n\n" +

                        "Connectez-vous à Gonifty pour l'accepter ou la refuser :\n" +
                        rentalLink + "\n\n" +

                        "Merci,\n" +
                        "L'équipe Gonifty"
        );


        mailSender.send(mail);
    }
}
