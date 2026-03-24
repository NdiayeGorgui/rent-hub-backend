package com.smartiadev.messaging_service.controller;

import com.smartiadev.messaging_service.dto.FaqRequest;
import com.smartiadev.messaging_service.dto.FaqResponse;
import com.smartiadev.messaging_service.service.FaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    // ✅ Liste complète
    @GetMapping
    public List<FaqResponse> getAllFaqs() {
        return faqService.getAllFaqs();
    }

    // ✅ Par thème
    @GetMapping("/theme/{theme}")
    public List<FaqResponse> getFaqsByTheme(@PathVariable String theme) {
        return faqService.getFaqsByTheme(theme);
    }

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<FaqResponse> createFaq(
            @Valid @RequestBody FaqRequest request
    ) {
        return ResponseEntity.ok(faqService.createFaq(request));
    }

    // ✅ UPDATE (bonus 🔥)
    @PutMapping("/{id}")
    public ResponseEntity<FaqResponse> updateFaq(
            @PathVariable Long id,
            @Valid @RequestBody FaqRequest request
    ) {
        return ResponseEntity.ok(faqService.updateFaq(id, request));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/feedback")
    public void feedback(
            @PathVariable Long id,
            @RequestParam boolean helpful
    ) {
        faqService.feedback(id, helpful);
    }
}