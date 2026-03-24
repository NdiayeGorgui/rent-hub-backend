package com.smartiadev.auth_service.kafka;

import com.smartiadev.auth_service.service.AdminService;
import com.smartiadev.base_domain_service.dto.UserSuspendedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

@Component
@RequiredArgsConstructor
public class DisputeUserSuspendedConsumer {

    private final AdminService adminService;

    @KafkaListener(topics = "user.suspended", groupId = "auth-service")
    public void onUserSuspended(UserSuspendedEvent event) {
        adminService.suspendUser(event.userId());
    }

}
