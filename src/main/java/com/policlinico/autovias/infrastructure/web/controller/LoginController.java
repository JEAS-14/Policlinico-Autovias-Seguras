package com.policlinico.autovias.infrastructure.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.policlinico.autovias.application.service.RateLimitService;
import com.policlinico.autovias.domain.exception.RateLimitExceededException;
import com.policlinico.autovias.infrastructure.util.IpUtil;

@Controller
@RequiredArgsConstructor
public class LoginController {

    @Value("${app.mail.destinatario}")
    private String emailAutorizado;

    // Sin respaldo. La contraseña viene de secrets.properties (Local) o Railway
    // (Nube)
    @Value("${app.admin.password}")
    private String passwordAdmin;

    private final RateLimitService rateLimitService;

    @GetMapping("/admin/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("usuarioAutenticado") != null) {
            return "redirect:/admin";
        }
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String clientIp = IpUtil.getClientIp(request);

        // Verificar si la IP está bloqueada por demasiados intentos fallidos
        if (!rateLimitService.isRequestAllowed(clientIp, "LOGIN_FAILED")) {
            int remaining = rateLimitService.getRemainingRequests(clientIp, "LOGIN_FAILED");
            throw new RateLimitExceededException(remaining, 24);
        }

        // Validar credenciales
        if (email.equals(emailAutorizado) && password.equals(passwordAdmin)) {
            session.setAttribute("usuarioAutenticado", email);
            session.setAttribute("nombreUsuario", "Administrador");
            return "redirect:/admin";
        }

        // Registrar intento fallido
        rateLimitService.recordRequest(clientIp, "LOGIN_FAILED");

        redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas");
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada exitosamente");
        return "redirect:/admin/login";
    }
}