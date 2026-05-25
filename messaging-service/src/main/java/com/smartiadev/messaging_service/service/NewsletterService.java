package com.smartiadev.messaging_service.service;

import com.smartiadev.messaging_service.dto.NewsletterRequest;
import com.smartiadev.messaging_service.entity.NewsletterSubscriber;
import com.smartiadev.messaging_service.repository.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterRepository repository;
    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String fromEmail;

    public void subscribe(String email) {

        if (!repository.existsByEmail(email)) {

            repository.save(
                    NewsletterSubscriber.builder()
                            .email(email)
                            .active(true)
                            .build()
            );
        }
    }

    public void unsubscribe(String email) {
        repository.findByEmail(email).ifPresent(s -> {
            s.setActive(false);
            repository.save(s);
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
                        "https://app.gonifty.ca/newsletter/unsubscribe?email=" + sub.getEmail()
                );
                mailSender.send(mail);
            } catch (Exception e) {
                // Continue même si un email échoue
            }
        }
    }
}
