package com.smartiadev.messaging_service.service;

import com.smartiadev.messaging_service.dto.NewsletterRequest;
import com.smartiadev.messaging_service.entity.NewsletterSubscriber;
import com.smartiadev.messaging_service.repository.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterRepository repository;
    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String fromEmail;

    public void subscribe(String email) {

        repository.findByEmail(email).ifPresentOrElse(subscriber -> {

            // Si déjà existant mais désactivé → réactiver
            if (!subscriber.isActive()) {

                subscriber.setActive(true);

                subscriber.setSubscribedAt(LocalDateTime.now());

                repository.save(subscriber);

                try {

                    SimpleMailMessage mail = new SimpleMailMessage();

                    mail.setFrom(fromEmail);

                    mail.setTo(email);

                    mail.setSubject("Réabonnement à l’infolettre Gonifty 🎉");

                    mail.setText(
                            "Bonjour,\n\n" +
                                    "Votre abonnement à l’infolettre Gonifty a été réactivé.\n\n" +
                                    "Merci de nous rejoindre à nouveau ❤️\n\n" +
                                    "L’équipe Gonifty"
                    );

                    mailSender.send(mail);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, () -> {

            // Nouvel abonnement
            repository.save(
                    NewsletterSubscriber.builder()
                            .email(email)
                            .active(true)
                            .subscribedAt(LocalDateTime.now())
                            .build()
            );

            try {

                SimpleMailMessage mail = new SimpleMailMessage();

                mail.setFrom(fromEmail);

                mail.setTo(email);

                mail.setSubject("Bienvenue dans l’infolettre Gonifty 🎉");

                mail.setText(
                        "Bonjour,\n\n" +
                                "Merci de vous être inscrit à l’infolettre Gonifty.\n\n" +
                                "Vous recevrez :\n" +
                                "- les meilleures annonces\n" +
                                "- les nouvelles enchères\n" +
                                "- des conseils\n" +
                                "- les nouveautés Gonifty\n\n" +
                                "Merci ❤️\n\n" +
                                "L’équipe Gonifty"
                );

                mailSender.send(mail);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void unsubscribe(String email) {

        repository.findByEmail(email).ifPresent(subscriber -> {

            subscriber.setActive(false);

            repository.save(subscriber);

            try {

                SimpleMailMessage mail = new SimpleMailMessage();

                mail.setFrom(fromEmail);

                mail.setTo(email);

                mail.setSubject("Désabonnement à l’infolettre Gonifty");

                mail.setText(
                        "Bonjour,\n\n" +
                                "Votre désabonnement à l’infolettre Gonifty a bien été pris en compte.\n\n" +
                                "Nous sommes désolés de vous voir partir 😢\n\n" +
                                "Vous pouvez vous réabonner à tout moment sur Gonifty.\n\n" +
                                "L’équipe Gonifty ❤️"
                );

                mailSender.send(mail);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> getSubscribers() {
        return repository.findByActiveTrue()
                .stream().map(NewsletterSubscriber::getEmail).toList();
    }

    public void sendNewsletter(NewsletterRequest request) {
        List<NewsletterSubscriber> subscribers = repository.findByActiveTrue();
        for (NewsletterSubscriber sub : subscribers) {
            try {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setFrom(fromEmail);
                mail.setTo(sub.getEmail());
                mail.setSubject(request.subject());
                mail.setText(request.body() +
                        "\n\n---\nPour vous désabonner : " +
                        "https://app.gonifty.ca/news-letter/unsubscribe?email=" + sub.getEmail()
                );
                mailSender.send(mail);
            } catch (Exception e) {
                // Continue même si un email échoue
            }
        }
    }

    public boolean isSubscribed(String email) {
        return repository.findByEmail(email)
                .map(NewsletterSubscriber::isActive)
                .orElse(false);
    }
}
