package com.smartiadev.messaging_service.repository;

import com.smartiadev.messaging_service.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByTimestampAsc(Long conversationId);

    long countByConversationIdAndReceiverIdAndReadFalse(
            Long conversationId,
            UUID receiverId
    );

    long countByReceiverIdAndReadFalse(UUID receiverId);

}