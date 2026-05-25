package com.smartiadev.messaging_service.controller;


import com.smartiadev.messaging_service.dto.NewsletterRequest;
import com.smartiadev.messaging_service.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/newsletter")
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;

    // Public — s'abonner
    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestBody Map<String, String> body) {
        newsletterService.subscribe(body.get("email"));
        return ResponseEntity.ok().build();
    }

    // Public — se désabonner via lien email
    @GetMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam String email) {
        newsletterService.unsubscribe(email);
        return ResponseEntity.ok("Vous avez été désabonné avec succès.");
    }

    // Admin — envoyer une newsletter
    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody NewsletterRequest request) {
        newsletterService.sendNewsletter(request);
        return ResponseEntity.ok().build();
    }

    // Admin — liste des abonnés
    @GetMapping("/subscribers")
    public ResponseEntity<List<String>> getSubscribers() {
        return ResponseEntity.ok(newsletterService.getSubscribers());
    }
}
