package com.policlinico.autovias.infrastructure.web.controller;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {
    
    @Value("${app.mail.destinatario}")
    private String emailAutorizado;
    
    @Value("${app.admin.password:admin123}")
    private String passwordAdmin;
    
    @GetMapping("/admin/login")
    public String mostrarLogin(HttpSession session) {
        // Si ya está logueado, redirigir al panel
        if (session.getAttribute("usuarioAutenticado") != null) {
            return "redirect:/admin/consultas";
        }
        return "admin/login";
    }
    
    @PostMapping("/admin/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Validar credenciales
        if (email.equals(emailAutorizado) && password.equals(passwordAdmin)) {
            session.setAttribute("usuarioAutenticado", email);
            session.setAttribute("nombreUsuario", "Administrador");
            return "redirect:/admin/consultas";
        }
        
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