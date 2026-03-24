package com.smartiadev.messaging_service.service;

import com.smartiadev.messaging_service.dto.FaqRequest;
import com.smartiadev.messaging_service.dto.FaqResponse;
import com.smartiadev.messaging_service.entity.Faq;
import com.smartiadev.messaging_service.mapper.FaqMapper;
import com.smartiadev.messaging_service.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    // ✅ GET ALL
    public List<FaqResponse> getAllFaqs() {
        return faqRepository.findAll()
                .stream()
                .map(FaqMapper::toResponse)
                .toList();
    }

    // ✅ GET BY THEME
    public List<FaqResponse> getFaqsByTheme(String theme) {
        return faqRepository.findByThemeIgnoreCase(theme)
                .stream()
                .map(FaqMapper::toResponse)
                .toList();
    }

    // ✅ CREATE
    public FaqResponse createFaq(FaqRequest request) {

        Faq faq = FaqMapper.toEntity(request);

        Faq saved = faqRepository.save(faq);

        return FaqMapper.toResponse(saved);
    }

    // ✅ DELETE
    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    // ✅ UPDATE (bonus 🔥)
    public FaqResponse updateFaq(Long id, FaqRequest request) {

        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));

        FaqMapper.updateEntity(faq, request);

        Faq updated = faqRepository.save(faq);

        return FaqMapper.toResponse(updated);
    }

    public void feedback(Long id, boolean helpful) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));

        if (helpful) {
            faq.setHelpfulCount(faq.getHelpfulCount() + 1);
        } else {
            faq.setNotHelpfulCount(faq.getNotHelpfulCount() + 1);
        }

        faqRepository.save(faq);
    }
}