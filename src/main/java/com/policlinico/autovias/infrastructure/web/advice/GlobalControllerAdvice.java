package com.policlinico.autovias.infrastructure.web.advice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Advice global que inyecta atributos en todas las vistas
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${analytics.ga4.id:}")
    private String analyticsId;

    /**
     * Inyecta el ID de Google Analytics en todas las vistas
     * Si no está configurado, devuelve string vacío
     */
    @ModelAttribute("analyticsId")
    public String addAnalyticsId() {
        return analyticsId;
    }
}
