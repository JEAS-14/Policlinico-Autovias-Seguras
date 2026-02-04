package com.policlinico.autovias.infrastructure.web.controller;

import com.policlinico.autovias.application.dto.ArticuloBlogDTO;
import com.policlinico.autovias.application.dto.ReclamacionDTO;
import com.policlinico.autovias.application.service.BlogService;
import com.policlinico.autovias.application.service.EmailService;
import com.policlinico.autovias.application.service.GoogleSheetsService;
import com.policlinico.autovias.application.service.ReclamacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Controlador principal para las páginas públicas
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final EmailService emailService;
    private final GoogleSheetsService googleSheetsService;
    private final ReclamacionService reclamacionService;
    private final BlogService blogService;
    
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
     * Blog dinámico desde Google Sheets
     * URL: http://localhost:8080/nuestroBlog
     */
    @GetMapping("/nuestroBlog")
    public String nuestroBlog(Model model) {
        try {
            // Obtener artículo destacado (grande arriba)
            ArticuloBlogDTO destacado = blogService.obtenerArticuloDestacado();
            
            // Obtener artículos normales (grid de tarjetas)
            List<ArticuloBlogDTO> articulos = blogService.obtenerArticulosNormales();
            
            model.addAttribute("destacado", destacado);
            model.addAttribute("articulos", articulos);
            
            log.info("Blog cargado: {} artículos (1 destacado, {} normales)", 
                     articulos.size() + (destacado != null ? 1 : 0), 
                     articulos.size());
            
        } catch (Exception e) {
            log.error("Error al cargar blog", e);
            model.addAttribute("error", "No se pudieron cargar los artículos en este momento");
        }
        
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
            // Delegar al servicio que maneja todo: DB, Google Sheets (backup) y Emails
            reclamacionService.crearReclamacion(reclamacion);
            model.addAttribute("success", "Su reclamación ha sido enviada exitosamente. Recibirá una confirmación por email y una respuesta en un plazo máximo de 15 días hábiles.");
        } catch (Exception e) {
            log.error("Error al procesar reclamación", e);
            model.addAttribute("error", "Hubo un error al enviar la reclamación. Por favor, inténtelo más tarde.");
        }

        return "libroReclamaciones";
    }
}