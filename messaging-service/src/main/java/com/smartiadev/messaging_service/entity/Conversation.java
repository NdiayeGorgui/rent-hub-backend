package com.smartiadev.messaging_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "conversations", schema = "message_schema")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // participant 1
    private UUID user1Id;

    // participant 2
    private UUID user2Id;

    // objet concerné (location ou enchère)
    private Long itemId;

    private LocalDateTime createdAt;

    private String lastMessage;
    //permet de trier les conversations
    private LocalDateTime lastMessageAt;

    //Cela permet au frontend d'afficher :
    //
    //Vous : Bonjour
    private UUID lastSenderId;

    // relation 1 -> N
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;
}