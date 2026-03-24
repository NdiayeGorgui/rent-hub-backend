package com.smartiadev.messaging_service.repository;


import com.smartiadev.messaging_service.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByThemeIgnoreCase(String theme);
}
