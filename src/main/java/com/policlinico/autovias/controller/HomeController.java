package com.policlinico.autovias.controller;

import org. springframework.stereotype.Controller;
import org.springframework.web.bind. annotation.GetMapping;

/**
 * Controlador principal para las páginas públicas
 */
@Controller
public class HomeController {
    
    /**
     * Página principal / Landing page
     * URL: http://localhost:8080/
     */
    @GetMapping("/")
    public String index() {
        return "home";
    }
    
    /**
     * Ruta alternativa para home
     * URL: http://localhost:8080/home
     */
    @GetMapping("/home")
    public String home() {
        return "home";
    }
    
    /**
     * Página Sobre Nosotros
     * URL: http://localhost:8080/nosotros
     */
    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros"; // templates/Nosotros. html (con mayúscula)
    }
    
    /**
     * Página de Servicios
     * URL:  http://localhost:8080/servicios
     */
    @GetMapping("/nuestrosServicios")
    public String nuestrosServicios() {
        return "nuestrosServicios";
    }
    
    /**
     * Preguntas Frecuentes
     * URL: http://localhost:8080/preguntas-frecuentes
     */
    @GetMapping("/preguntasFrecuentes")
    public String preguntasFrecuentes() {
        return "preguntasFrecuentes";
    }
    
    /**
     * Blog
     * URL: http://localhost:8080/blog
     */
    @GetMapping("/nuestroBlog")
    public String nuestroBlog() {
        return "nuestroBlog";
    }
}