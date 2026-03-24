package com.smartiadev.messaging_service.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SendMessageRequest {

    private UUID receiverId;
    private Long itemId;
    private String content;

}