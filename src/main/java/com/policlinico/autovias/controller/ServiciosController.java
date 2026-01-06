package com.policlinico.autovias.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web. bind.annotation.RequestMapping;

/**
 * Controlador para las páginas de servicios específicos
 */
@Controller
@RequestMapping("/servicios")
public class ServiciosController {
    
    /**
     * Examen Médico Ocupacional (EMO)
     * URL: http://localhost:8080/servicios/examen-ocupacional
     */
    @GetMapping("/examen-ocupacional")
    public String examenOcupacional() {
        return "/examenMedicoOcupacional";
    }
    
    /**
     * Examen Médico para Brevete (Licencia de Conducir)
     * URL: http://localhost:8080/servicios/examen-brevete
     */
    @GetMapping("/examen-brevete")
    public String examenBrevete() {
        return "/examenMedicoBrevete";
    }
    
    /**
     * Examen SUCAMEC
     * URL:  http://localhost:8080/servicios/examen-sucamec
     */
    @GetMapping("/examen-sucamec")
    public String examenSucamec() {
        return "/examenSucamec";
    }
    
    /**
     * Escuela de Conductores
     * URL: http://localhost:8080/servicios/escuela-conductores
     */
    @GetMapping("/escuela-conductores")
    public String escuelaConductores() {
        return "/escuelaConductores";
    }
}