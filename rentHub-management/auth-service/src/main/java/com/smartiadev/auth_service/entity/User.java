package com.smartiadev.auth_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "auth_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;
    private String phone;
    private String city;
    private String profilePicture;

    private boolean enabled = true;
    private int auctionStrikes = 0;
    private boolean auctionRestricted = false;

    private LocalDateTime createdAt;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles;
}

