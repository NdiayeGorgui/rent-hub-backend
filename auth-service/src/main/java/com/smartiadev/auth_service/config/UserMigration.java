/*
package com.smartiadev.auth_service.config;

import com.smartiadev.auth_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMigration {

    private final UserRepository userRepository;

    @PostConstruct
    public void enableExistingUsers() {
        userRepository.enableAllDisabledUsers();
    }
}

*/
