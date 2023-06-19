package ru.panic.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/auth/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "PUT")
                .allowedHeaders("*");
        registry.addMapping("/api/v1/getInfoByJwt")
                .allowedOrigins("http://localhost:8081", "http://localhost:8083", "http://localhost:9000")
                .allowedMethods("POST")
                .allowedHeaders("*");
    }
}
