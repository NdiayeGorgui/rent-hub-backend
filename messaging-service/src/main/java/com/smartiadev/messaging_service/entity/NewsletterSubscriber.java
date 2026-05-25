package com.smartiadev.messaging_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "newsletter_subscribers", schema = "message_schema")
public class NewsletterSubscriber {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    private boolean active = true;
    private LocalDateTime subscribedAt = LocalDateTime.now();
}
