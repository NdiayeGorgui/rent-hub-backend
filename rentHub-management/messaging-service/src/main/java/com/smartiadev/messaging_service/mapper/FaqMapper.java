package com.smartiadev.messaging_service.mapper;

import com.smartiadev.messaging_service.dto.FaqRequest;
import com.smartiadev.messaging_service.dto.FaqResponse;
import com.smartiadev.messaging_service.entity.Faq;

public class FaqMapper {

    // 🔁 Entity → DTO
    public static FaqResponse toResponse(Faq faq) {
        return FaqResponse.builder()
                .id(faq.getId())
                .theme(faq.getTheme())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .build();
    }

    // 🔁 DTO → Entity
    public static Faq toEntity(FaqRequest request) {
        return Faq.builder()
                .theme(request.theme())
                .question(request.question())
                .answer(request.answer())
                .build();
    }

    // 🔁 Update Entity
    public static void updateEntity(Faq faq, FaqRequest request) {
        faq.setTheme(request.theme());
        faq.setQuestion(request.question());
        faq.setAnswer(request.answer());
    }
}