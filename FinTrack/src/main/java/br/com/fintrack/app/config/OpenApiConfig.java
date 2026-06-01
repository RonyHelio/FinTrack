package br.com.fintrack.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI finTrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinTrack API")
                        .description("""
                                API REST do **FinTrack** — aplicativo de controle financeiro pessoal.
                                
                                ## Autenticação
                                1. Registre-se em `POST /api/auth/register`
                                2. Faça login em `POST /api/auth/login` e copie o campo **token**
                                3. Clique em **Authorize** (cadeado) e cole o token no formato: `Bearer {token}`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FinTrack — PoC Acadêmica")
                                .email("fintrack@academico.br")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Insira o JWT gerado pelo endpoint de login. Formato: Bearer {token}")));
    }
}
