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
        return "Nosotros"; // templates/Nosotros. html (con mayúscula)
    }
    
    /**
     * Página de Servicios
     * URL:  http://localhost:8080/servicios
     */
    @GetMapping("/servicios")
    public String servicios() {
        return "nuestrosServicios";
    }
    
    /**
     * Preguntas Frecuentes
     * URL: http://localhost:8080/preguntas-frecuentes
     */
    @GetMapping("/preguntas-frecuentes")
    public String preguntasFrecuentes() {
        return "preguntasFrecuentes";
    }
    
    /**
     * Blog
     * URL: http://localhost:8080/blog
     */
    @GetMapping("/blog")
    public String blog() {
        return "nuestroBlog";
    }
}