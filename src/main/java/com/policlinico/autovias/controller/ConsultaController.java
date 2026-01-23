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
import com.policlinico.autovias.model.dto.ConsultaDTO;
import com.policlinico.autovias.service.EmailService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/consulta")
public class ConsultaController {

    private final EmailService emailService;

    public ConsultaController(EmailService emailService) {
        this.emailService = emailService;
    }

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

        String ticket = "C-" + System.currentTimeMillis();

        emailService.enviarNotificacionConsulta(consultaDTO, ticket);
        emailService.enviarConfirmacionCliente(consultaDTO, ticket);

        return "redirect:/consulta/confirmacion";
    }

    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "confirmacion";
    }
}
