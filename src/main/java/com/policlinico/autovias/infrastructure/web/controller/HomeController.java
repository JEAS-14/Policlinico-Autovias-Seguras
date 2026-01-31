package com.policlinico.autovias.infrastructure.web.controller;

import com.policlinico.autovias.application.dto.ReclamacionDTO;
import com.policlinico.autovias.application.service.EmailService;
import com.policlinico.autovias.application.service.GoogleSheetsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador principal para las páginas públicas
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EmailService emailService;
    private final GoogleSheetsService googleSheetsService;
    
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
        return "nosotros";
    }
    
    /**
     * Página de Servicios
     * URL: http://localhost:8080/nuestrosServicios
     */
    @GetMapping("/nuestrosServicios")
    public String nuestrosServicios() {
        return "nuestrosServicios";
    }
    
    /**
     * Preguntas Frecuentes
     * URL: http://localhost:8080/preguntasFrecuentes
     */
    @GetMapping("/preguntasFrecuentes")
    public String preguntasFrecuentes() {
        return "preguntasFrecuentes";
    }
    
    /**
     * Blog
     * URL: http://localhost:8080/nuestroBlog
     */
    @GetMapping("/nuestroBlog")
    public String nuestroBlog() {
        return "nuestroBlog";
    }


    @GetMapping("/politicaPrivacidad")
    public String politicaPrivacidad() {
        return "politicaPrivacidad";
    }

    // Ruta para Términos y Condiciones
    @GetMapping("/terminosCondiciones")
    public String terminosCondiciones() {
        return "terminosCondiciones";
    }

    // Ruta para el Libro de Reclamaciones
    @GetMapping("/libroReclamaciones")
    public String libroReclamaciones() {
        return "libroReclamaciones";
    }

    // Procesar el formulario de reclamaciones
    @PostMapping("/libroReclamaciones")
    public String procesarReclamacion(@Valid ReclamacionDTO reclamacion, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Por favor, complete todos los campos obligatorios correctamente.");
            return "libroReclamaciones";
        }

        try {
            // Generar número de ticket único
            String numeroTicket = "REC-" + System.currentTimeMillis();
            
            // Guardar en Google Sheets
            googleSheetsService.guardarReclamacion(
                    reclamacion.getNombre(),
                    reclamacion.getApellido(),
                    reclamacion.getDniCe(),
                    reclamacion.getEmail(),
                    reclamacion.getTelefono(),
                    reclamacion.getDomicilio(),
                    reclamacion.getDetalle(),
                    reclamacion.getPedido(),
                    numeroTicket
            );
            
            // Enviar emails
            emailService.enviarNotificacionReclamacion(reclamacion);
            emailService.enviarConfirmacionReclamante(reclamacion);
            model.addAttribute("success", "Su reclamación ha sido enviada exitosamente. Recibirá una confirmación por email y una respuesta en un plazo máximo de 15 días hábiles.");
        } catch (Exception e) {
            model.addAttribute("error", "Hubo un error al enviar la reclamación. Por favor, inténtelo más tarde.");
        }

        return "libroReclamaciones";
    }
}