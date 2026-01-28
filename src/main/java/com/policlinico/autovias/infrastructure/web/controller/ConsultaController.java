package com.policlinico.autovias.infrastructure.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.policlinico.autovias.application.dto.ConsultaDTO;
import com.policlinico.autovias.application.service.ConsultaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/consulta")
@RequiredArgsConstructor // Simplifica el constructor para inyección
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
            Model model) {

        if (result.hasErrors()) {
            return "formulario";
        }

        // Delegamos TODO al servicio: Crear ticket, Guardar en DB y Enviar Emails
        consultaService.crearConsulta(consultaDTO);

        return "redirect:/consulta/confirmacion";
    }

    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "confirmacion";
    }
}