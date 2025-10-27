package com.example.cinema.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuração do Spring MVC para servir arquivos estáticos de um diretório
 * configurável. O mapeamento abaixo expõe o conteúdo do diretório definido em
 * {@code upload.dir} para a URL /uploads/**, permitindo que as imagens
 * carregadas fiquem acessíveis via browser.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Diretório onde as imagens de cartaz são salvas. Vem do application.properties.
     */
    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Converte o diretório relativo para um caminho absoluto
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String uploadAbsolutePath = uploadPath.toUri().toString();
        // Mapeia todas as requisições /uploads/** para arquivos no diretório uploadDir
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadAbsolutePath);
    }
}