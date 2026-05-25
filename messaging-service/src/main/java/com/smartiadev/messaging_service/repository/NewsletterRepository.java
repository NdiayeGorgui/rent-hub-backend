package com.smartiadev.messaging_service.repository;

import com.smartiadev.messaging_service.entity.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<NewsletterSubscriber, Long> {

    // Vérifie si un email existe déjà
    boolean existsByEmail(String email);

    // Trouver un subscriber par email
    Optional<NewsletterSubscriber> findByEmail(String email);

    // Tous les abonnés actifs
    List<NewsletterSubscriber> findByActiveTrue();

    // Tous les abonnés inactifs
    List<NewsletterSubscriber> findByActiveFalse();

    // Trouver un abonné actif par email
    Optional<NewsletterSubscriber> findByEmailAndActiveTrue(String email);

    // Compter les abonnés actifs
    long countByActiveTrue();

    // Compter les abonnés inactifs
    long countByActiveFalse();

    // Vérifie si un email est actif
    boolean existsByEmailAndActiveTrue(String email);

    // Vérifie si un email est inactif
    boolean existsByEmailAndActiveFalse(String email);
}