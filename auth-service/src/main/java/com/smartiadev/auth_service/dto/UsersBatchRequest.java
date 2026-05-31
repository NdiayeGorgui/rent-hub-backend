package com.smartiadev.auth_service.dto;

import java.util.List;
import java.util.UUID;

public record UsersBatchRequest(
        List<UUID> ids
) {}
