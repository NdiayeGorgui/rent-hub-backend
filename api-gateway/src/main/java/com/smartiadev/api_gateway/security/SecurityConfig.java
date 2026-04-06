package com.smartiadev.api_gateway.security;

import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // ✅ ACTIVE CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeExchange(exchange -> exchange

                        // ✅ IMPORTANT : autoriser OPTIONS (preflight)
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/actuator/**").permitAll()

                        // =========================
                        // 🔓 SWAGGER PUBLIC
                        // =========================
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                        // =========================
                        // 🔓 MICROSERVICES DOCS
                        // =========================
                        .pathMatchers(
                                "/auth-service/v3/api-docs",
                                "/item-service/v3/api-docs",
                                "/rental-service/v3/api-docs",
                                "/review-service/v3/api-docs",
                                "/payments-service/v3/api-docs",
                                "/dispute-service/v3/api-docs",
                                "/notif-service/v3/api-docs",
                                "/subscription-service/v3/api-docs",
                                "/auction-service/v3/api-docs",
                                "/messaging-service/v3/api-docs"
                        ).permitAll()

                        .pathMatchers("/api/payments/stripe/webhook").permitAll()

                        // 🔓 AUTH PUBLIC
                        .pathMatchers(
                                "/api/auth/**",
                                "/api/users/internal/*/auction-strike",
                                "/api/profile/**"
                        ).permitAll()
                        .pathMatchers(
                                "/ws-notifications/**",
                                "/ws-chat/**"
                        ).permitAll()
                        // 🔥 Autoriser les images
                        .pathMatchers("/uploads/**").permitAll()
                        .pathMatchers(
                                "/api/subscriptions/internal/**",
                                "/api/notifications/me",
                                "/api/messages/**",
                                "/api/faqs/**"
                        ).permitAll()

                        // 🔐 ADMIN
                        .pathMatchers("/api/admin/**").hasRole("ADMIN")

                        // 🔐 AUTHENTIFIED
                        .pathMatchers(
                                "/api/items/**",
                                "/api/rentals/**",
                                "/api/reviews/**",
                                "/api/notifications/**",
                                "/api/disputes/**",
                                "/api/payments/**",
                                "/api/subscriptions/**",
                                "/api/auctions/**",
                                "/api/messages/**"
                        ).authenticated()

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles"); // ton claim JWT pour les rôles
        authoritiesConverter.setAuthorityPrefix("");             // enlève "ROLE_" par défaut

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }



    @PostConstruct
    public void init() {
        System.out.println("JWT SECRET = " + jwtSecret);
        System.out.println("JWT SECRET LENGTH = " + (jwtSecret != null ? jwtSecret.length() : "NULL"));
    }
}