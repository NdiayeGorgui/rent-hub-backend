package com.smartiadev.auth_service.controller;

import com.smartiadev.auth_service.dto.request.LoginRequest;
import com.smartiadev.auth_service.dto.request.RegisterRequest;
import com.smartiadev.auth_service.dto.response.AuthResponse;
import com.smartiadev.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns an authentication response containing the access token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("permitAll()")
    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }


    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user using email/username and password, and returns a JWT authentication token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials format"),
            @ApiResponse(responseCode = "401", description = "Invalid email/username or password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}

