package com.smartiadev.messaging_service.controller;

import com.smartiadev.messaging_service.dto.ContactRequest;
import com.smartiadev.messaging_service.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ContactController {

    private final MessageService messageService;

    @PostMapping("/contact")
    public ResponseEntity<Void> sendContact(
            @RequestBody @Valid ContactRequest request
    ) {

        messageService.sendContact(request);

        return ResponseEntity.ok().build();
    }
}