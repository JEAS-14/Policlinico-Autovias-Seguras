package com.policlinico.autovias.controller;

import com.policlinico.autovias.model.dto.ConsultaDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Controlador para formularios de consulta y cotización
 */
@Controller
@RequestMapping("/consulta")
public class ConsultaController {

    /**
     * Mostrar formulario de consulta/cotización
     * URL: http://localhost:8080/consulta/formulario
     */
    @GetMapping("/formulario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("consultaDTO", new ConsultaDTO());
        return "formulario";
    }

    /**
     * Ruta alternativa: /cotizar
     * URL: http://localhost:8080/consulta/cotizar
     */
    @GetMapping("/cotizar")
    public String cotizar(Model model) {
        model.addAttribute("consultaDTO", new ConsultaDTO());
        return "formulario";
    }

    /**
     * Procesar el formulario de consulta
     * URL: POST http://localhost:8080/consulta/enviar
     */
    @PostMapping("/enviar")
    public String procesarConsulta(
            @Valid @ModelAttribute("consultaDTO") ConsultaDTO consultaDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validar errores
        if (result.hasErrors()) {
            return "formulario";
        }

        // TODO: Aquí iría la lógica para:
        // - Guardar en base de datos
        // - Enviar email de notificación
        // - Enviar WhatsApp (opcional)

        System.out.println("Consulta recibida de: " + consultaDTO.getNombre());
        System.out.println("Email: " + consultaDTO.getEmail());
        System.out.println("Tipo:  " + consultaDTO.getTipoConsulta());

        // Mensaje de éxito
        redirectAttributes.addFlashAttribute("mensaje",
                "¡Gracias por contactarnos! Nos comunicaremos contigo pronto.");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        return "redirect:/consulta/confirmacion";
    }

    /**
     * Página de confirmación después de enviar formulario
     * URL: http://localhost:8080/consulta/confirmacion
     */
    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "confirmacion"; // Necesitarás crear este archivo
    }

    /**
     * Consultar resultados
     * URL: http://localhost:8080/consulta/resultados
     */
    @GetMapping("/resultados")
    public String resultados() {
        return "resultados"; // Necesitarás crear este archivo
    }
}