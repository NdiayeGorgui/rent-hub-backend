package com.smartiadev.api_gateway.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ===== DEV =====
        config.addAllowedOrigin("http://localhost:8181");
        config.addAllowedOrigin("http://renthub-mobile:8181");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://renthub-web:3000");
        config.addAllowedOrigin("http://192.168.0.118:8181");
        config.addAllowedOrigin("http://10.0.2.2:8181");
        config.addAllowedOrigin("http://192.168.0.118:9191"); // ← ajoute
        config.addAllowedOrigin("http://10.0.2.2:9191");      // ← ajoute
        config.addAllowedOrigin("http://localhost:9191");      // ← ajoute

        // ===== PROD =====
        config.addAllowedOrigin("https://gonifty.ca");
        config.addAllowedOrigin("https://www.gonifty.ca");
        config.addAllowedOrigin("https://api.gonifty.ca");
       

        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}