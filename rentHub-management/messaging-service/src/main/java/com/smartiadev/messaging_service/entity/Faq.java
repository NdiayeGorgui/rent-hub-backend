package com.smartiadev.messaging_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "faqs", schema = "message_schema")
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String theme;   // ex: "Paiement", "Rendez-vous", "Messagerie"
    private String question;
    @Column(length = 2000)
    private String answer;
    private int helpfulCount;
    private int notHelpfulCount;
}

