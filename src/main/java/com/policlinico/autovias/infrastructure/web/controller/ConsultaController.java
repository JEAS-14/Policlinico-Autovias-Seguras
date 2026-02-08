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
import com.policlinico.autovias.application.service.RateLimitService;
import com.policlinico.autovias.domain.exception.RateLimitExceededException;
import com.policlinico.autovias.infrastructure.util.IpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/consulta")
@RequiredArgsConstructor // Simplifica el constructor para inyección
public class ConsultaController {

    private final ConsultaService consultaService;
    private final RateLimitService rateLimitService;

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
            Model model,
            HttpServletRequest request) {

        if (result.hasErrors()) {
            return "formulario";
        }

        // Obtener IP del cliente
        String clientIp = IpUtil.getClientIp(request);

        // Verificar rate limit
        if (!rateLimitService.isRequestAllowed(clientIp, "CONSULTA")) {
            int remaining = rateLimitService.getRemainingRequests(clientIp, "CONSULTA");
            throw new RateLimitExceededException(remaining, 24);
        }

        // Registrar la solicitud
        rateLimitService.recordRequest(clientIp, "CONSULTA");

        // Delegamos TODO al servicio: Crear ticket, Guardar en DB y Enviar Emails
        consultaService.crearConsulta(consultaDTO);

        return "redirect:/consulta/confirmacion";
    }

    @GetMapping("/confirmacion")
    public String confirmacion() {
        return "confirmacion";
    }
}