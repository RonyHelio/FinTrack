package br.com.fintrack.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS é configurado em SecurityConfig para integração com Spring Security.
    // Esta classe pode ser usada para configurações adicionais do MVC no futuro.
}
