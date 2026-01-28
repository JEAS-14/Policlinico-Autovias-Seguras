package com.policlinico.autovias.infrastructure.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para las páginas de servicios específicos
 */
@Controller
@RequestMapping("/servicios")
public class ServiciosController {
    
    /**
     * Examen Médico Ocupacional (EMO)
     * URL: /servicios/examenMedicoOcupacional
     */
    @GetMapping("/examenMedicoOcupacional")
    public String examenMedicoOcupacional() {
        return "examenMedicoOcupacional"; // Sin la barra inicial
    }
    
    /**
     * Examen Médico para Brevete (Licencia de Conducir)
     * URL: /servicios/examenMedicoBrevete
     */
    @GetMapping("/examenMedicoBrevete")
    public String examenMedicoBrevete() {
        return "examenMedicoBrevete"; // Sin la barra inicial
    }
    
    /**
     * Examen SUCAMEC
     * URL: /servicios/examenSucamec
     */
    @GetMapping("/examenSucamec")
    public String examenSucamec() {
        return "examenSucamec"; // Sin la barra inicial
    }
    
    /**
     * Escuela de Conductores
     * URL: /servicios/escuelaConductores
     */
    @GetMapping("/escuelaConductores")
    public String escuelaConductores() {
        return "escuelaConductores"; // Sin la barra inicial
    }
}