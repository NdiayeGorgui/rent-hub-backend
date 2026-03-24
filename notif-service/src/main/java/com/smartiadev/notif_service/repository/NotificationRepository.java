package com.smartiadev.notif_service.repository;

import com.smartiadev.notif_service.entity.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Transactional
    @Modifying
    @Query("""
    update Notification n
    set n.read = true
    where n.userId = :userId
""")
    void markAllAsRead(UUID userId);



}

