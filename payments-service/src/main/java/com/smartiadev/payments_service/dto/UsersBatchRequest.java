package com.smartiadev.payments_service.dto;

import java.util.List;
import java.util.UUID;

public record UsersBatchRequest(
        List<UUID> ids
) {}
