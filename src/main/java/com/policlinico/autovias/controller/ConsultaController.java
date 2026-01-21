package com.policlinico.autovias.controller;

import com.policlinico.autovias.model.dto.ConsultaDTO;
import com.policlinico.autovias.model.entity.Consulta;
import com.policlinico.autovias.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/consulta")
@RequiredArgsConstructor
public class ConsultaController {
    
    private final ConsultaService consultaService;
    
    @GetMapping("/formulario")
    public String mostrarFormulario(Model model) {
        model.addAttribute("consultaDTO", new ConsultaDTO());
        return "formulario";
    }
    
    @GetMapping("/cotizar")
    public String cotizar(Model model) {
        model.addAttribute("consultaDTO", new ConsultaDTO());
        return "formulario";
    }
    
    @PostMapping("/enviar")
    public String procesarConsulta(
            @Valid @ModelAttribute("consultaDTO") ConsultaDTO consultaDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "formulario";
        }
        
        try {
            Consulta consulta = consultaService.crearConsulta(consultaDTO);
            
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("numeroTicket", consulta.getNumeroTicket());
            redirectAttributes.addFlashAttribute("mensaje", 
                "¡Gracias por contactarnos! Tu consulta ha sido registrada con el ticket " + 
                consulta.getNumeroTicket() + ". Te responderemos a la brevedad.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Hubo un error al procesar tu consulta. Por favor, intenta nuevamente o contáctanos directamente.");
        }
        
        return "redirect:/consulta/confirmacion";
    }
    
    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "confirmacion";
    }
}