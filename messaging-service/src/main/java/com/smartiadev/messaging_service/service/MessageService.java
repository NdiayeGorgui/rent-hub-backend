package com.smartiadev.messaging_service.service;

import com.smartiadev.messaging_service.client.UserClient;
import com.smartiadev.messaging_service.dto.*;
import com.smartiadev.messaging_service.entity.Conversation;
import com.smartiadev.messaging_service.entity.Message;
import com.smartiadev.messaging_service.repository.ConversationRepository;
import com.smartiadev.messaging_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserClient userClient;
    private final ImageStorageService imageStorageService;

    public MessageResponse sendMessage(UUID senderId, MessageRequest request) {




        UUID receiverId = request.receiverId();

        // 1️⃣ vérifier conversation
        Conversation conversation = conversationRepository
                .findConversationBetweenUsersAndItem(senderId, receiverId, request.itemId())
                .orElseGet(() -> {

                    Conversation newConversation = Conversation.builder()
                            .user1Id(senderId)
                            .user2Id(receiverId)
                            .itemId(request.itemId())
                            .createdAt(LocalDateTime.now())
                            .build();

                    return conversationRepository.save(newConversation);
                });

        // 3️⃣ créer message
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .itemId(request.itemId())
                .conversation(conversation)
                .content(request.content())
                .timestamp(LocalDateTime.now())
                .read(false)
                .supportMessage(false)
                .build();

        messageRepository.save(message);

        // 4️⃣ mettre à jour la conversation (IMPORTANT)
        conversation.setLastMessage(request.content());
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastSenderId(senderId);

        conversationRepository.save(conversation);

        return new MessageResponse(
                message.getId(),
                conversation.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getItemId(),
                message.getContent(),
               null,
                message.getTimestamp(),
                message.isRead()
        );
    }

    public List<MessageResponse> getConversationMessages(Long conversationId) {

        return messageRepository
                .findByConversationIdOrderByTimestampAsc(conversationId)
                .stream()
                .map(m -> new MessageResponse(
                        m.getId(),
                        m.getConversation().getId(),
                        m.getSenderId(),
                        m.getReceiverId(),
                        m.getItemId(),
                        m.getContent(),
                        m.getImageUrl(),
                        m.getTimestamp(),
                        m.isRead()
                ))
                .toList();
    }

    public List<ConversationResponse> getUserConversations(UUID userId) {

        List<Conversation> conversations;

        if (userId.equals(getFirstAdminId())) {
            // Si admin, récupérer toutes les conversations où il est impliqué
            conversations = conversationRepository.findByUser1IdOrUser2IdOrderByLastMessageAtDesc(getFirstAdminId(), getFirstAdminId());
        } else {
            conversations = conversationRepository.findByUser1IdOrUser2IdOrderByLastMessageAtDesc(userId, userId);
        }

        return conversations.stream()
                .map(conversation -> {

                    long unread = messageRepository
                            .countByConversationIdAndReceiverIdAndReadFalse(
                                    conversation.getId(),
                                    userId
                            );

                    String user1Username = "Unknown";
                    String user2Username = "Unknown";

                    try {
                        if (conversation.getUser1Id() != null) {
                            user1Username = userClient
                                    .getUserById(conversation.getUser1Id())
                                    .username();
                        }
                    } catch (Exception e) {
                        System.out.println("User1 fetch error: " + e.getMessage());
                    }

                    try {
                        if (conversation.getUser2Id() != null) {
                            user2Username = userClient
                                    .getUserById(conversation.getUser2Id())
                                    .username();
                        }
                    } catch (Exception e) {
                        System.out.println("User2 fetch error: " + e.getMessage());
                    }

                    return new ConversationResponse(
                            conversation.getId(),
                            conversation.getUser1Id(),
                            conversation.getUser2Id(),
                            user1Username,
                            user2Username,
                            conversation.getItemId(),
                            conversation.getLastMessage(),
                            conversation.getLastMessageAt(),
                            conversation.getLastSenderId(),
                            unread
                    );

                })
                .toList();
    }


    public Conversation getOrCreateSupportConversation(UUID userId) {
        UUID SUPPORT_ADMIN_ID = getFirstAdminId();

        // Vérifie si une conversation existe déjà entre l'utilisateur et le support
        return conversationRepository
                .findConversationBetweenUsersAndItem(userId, SUPPORT_ADMIN_ID, null)
                .orElseGet(() -> {
                    // Si pas existante, créer une nouvelle conversation "support"
                    Conversation c = new Conversation();
                    c.setUser1Id(userId);
                    c.setUser2Id(SUPPORT_ADMIN_ID);
                    c.setItemId(null); // conversation support n'est liée à aucun item
                    c.setLastMessage("");
                    c.setLastMessageAt(LocalDateTime.now());
                    c.setLastSenderId(userId);
                    return conversationRepository.save(c);
                });
    }

    public void markAsRead(Long messageId) {

        messageRepository.findById(messageId).ifPresent(message -> {
            System.out.println("MARK AS READ called for message: " + messageId);

            message.setRead(true);

            messageRepository.save(message);

        });

    }

    public MessageResponse sendSupportMessage(UUID senderId, SupportMessageRequest request) {

        UUID adminId = getFirstAdminId();

        // 🔥 identifier le user (toujours)
        UUID userId = senderId.equals(adminId)
                ? request.receiverId()
                : senderId;

        if (userId == null) {
            throw new RuntimeException("UserId is required for support conversation");
        }

        // 🔥 récupérer UNE seule conversation
        Conversation conversation = conversationRepository
                .findSupportConversation(userId, adminId)
                .orElseGet(() -> {

                    Conversation c = new Conversation();
                    c.setUser1Id(userId);     // TOUJOURS user
                    c.setUser2Id(adminId);    // TOUJOURS admin
                    c.setItemId(null);
                    c.setCreatedAt(LocalDateTime.now());

                    return conversationRepository.save(c);
                });

        // 🔥 déterminer receiver
        UUID receiverId = senderId.equals(adminId)
                ? userId
                : adminId;

        // 🔥 créer message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setItemId(null);
        message.setContent(request.content());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        message.setSupportMessage(true);

        messageRepository.save(message);

        // 🔥 UPDATE conversation (OBLIGATOIRE)
        conversation.setLastMessage(request.content());
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastSenderId(senderId);

        conversationRepository.save(conversation);

        return new MessageResponse(
                message.getId(),
                conversation.getId(), // 🔥 CRUCIAL
                message.getSenderId(),
                message.getReceiverId(),
                null,
                message.getContent(),
                message.getImageUrl(),
                message.getTimestamp(),
                message.isRead()
        );
    }
    public long getUnreadMessagesCount(UUID userId) {

        return messageRepository.countByReceiverIdAndReadFalse(userId);

    }

    public MessageResponse sendMessage(SendMessageRequest request) {

        UUID senderId = getCurrentUserId();
        UUID receiverId = request.getReceiverId();

        Conversation conversation;

        // 🔥 CAS SUPPORT (itemId = null)
        if (request.getItemId() == null) {

            conversation = conversationRepository
                    .findConversationBetweenUsersAndItem(
                            senderId,
                            receiverId,
                            null
                    )
                    .orElseGet(() -> {

                        Conversation newConv = new Conversation();
                        newConv.setUser1Id(senderId);
                        newConv.setUser2Id(receiverId);
                        newConv.setItemId(null);
                        newConv.setCreatedAt(LocalDateTime.now());

                        return conversationRepository.save(newConv);
                    });

        } else {

            // ✅ CAS NORMAL (avec item)
            conversation = conversationRepository
                    .findConversationBetweenUsersAndItem(
                            senderId,
                            receiverId,
                            request.getItemId()
                    )
                    .orElseGet(() -> {

                        Conversation newConv = new Conversation();
                        newConv.setUser1Id(senderId);
                        newConv.setUser2Id(receiverId);
                        newConv.setItemId(request.getItemId());
                        newConv.setCreatedAt(LocalDateTime.now());

                        return conversationRepository.save(newConv);
                    });
        }

        // 🔥 MESSAGE
        Message message = new Message();

        message.setConversation(conversation);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setItemId(request.getItemId());
        message.setContent(request.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        // ✅ détecter support automatiquement
        boolean isSupport = request.getItemId() == null;
        message.setSupportMessage(isSupport);

        messageRepository.save(message);

        // 🔥 UPDATE conversation (IMPORTANT pour inbox)
        conversation.setLastMessage(request.getContent());
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastSenderId(senderId);

        conversationRepository.save(conversation);

        return new MessageResponse(
                message.getId(),
                conversation.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getItemId(),
                message.getContent(),
                null,
                message.getTimestamp(),
                message.isRead()
        );
    }
    private UUID getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }

        return UUID.fromString(authentication.getName());
    }
    // -------------------
    // Méthode utilitaire pour récupérer le premier admin
    // -------------------
    private UUID getFirstAdminId() {
        var admins = userClient.getAdmins(); // List<UserDto>
        if (admins == null || admins.isEmpty()) {
            throw new IllegalStateException("No admin available");
        }
        return admins.get(0).id(); // prendre le premier admin
    }

    public MessageResponse sendWithImage(
            UUID senderId,
            Long conversationId,
            String content,        // ← texte optionnel
            MultipartFile image    // ← image optionnelle
    ) {
        // 1. Récupérer la conversation
        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // 2. Déterminer le receiver
        UUID receiverId = conversation.getUser1Id().equals(senderId)
                ? conversation.getUser2Id()
                : conversation.getUser1Id();

        // 3. Upload image si présente
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = imageStorageService.uploadImage(image);
        }

        // 4. Valider qu'on a au moins texte ou image
        if ((content == null || content.isBlank()) && imageUrl == null) {
            throw new RuntimeException("Message must have content or image");
        }

        // 5. Créer le message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setItemId(conversation.getItemId());
        message.setContent(content);
        message.setImageUrl(imageUrl);
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        message.setSupportMessage(false);
        messageRepository.save(message);

        // 6. Update conversation — lastMessage adapté
        String lastMessage = imageUrl != null && (content == null || content.isBlank())
                ? "📷 Image"
                : content;
        conversation.setLastMessage(lastMessage);
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastSenderId(senderId);
        conversationRepository.save(conversation);

        return new MessageResponse(
                message.getId(),
                conversation.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getItemId(),
                message.getContent(),
                message.getImageUrl(),
                message.getTimestamp(),
                message.isRead()
        );
    }
}