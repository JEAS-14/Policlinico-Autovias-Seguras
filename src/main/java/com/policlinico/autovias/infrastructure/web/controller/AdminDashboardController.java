package com.policlinico.autovias.infrastructure.web.controller;

import com.policlinico.autovias.application.service.BlogService;
import com.policlinico.autovias.application.service.ConsultaService;
import com.policlinico.autovias.domain.enums.EstadoConsulta;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final ConsultaService consultaService;
    private final BlogService blogService;
    
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        // Obtener nombre de usuario de la sesión
        String nombreUsuario = (String) session.getAttribute("nombreUsuario");
        model.addAttribute("nombreUsuario", nombreUsuario != null ? nombreUsuario : "Administrador");
        
        // Estadísticas de consultas
        long totalConsultas = consultaService.contarTodas();
        long consultasPendientes = consultaService.contarPorEstado(EstadoConsulta.PENDIENTE);
        long consultasEnProceso = consultaService.contarPorEstado(EstadoConsulta.EN_PROCESO);
        long consultasRespondidas = consultaService.contarPorEstado(EstadoConsulta.RESPONDIDA);
        
        model.addAttribute("totalConsultas", totalConsultas);
        model.addAttribute("consultasPendientes", consultasPendientes);
        model.addAttribute("consultasEnProceso", consultasEnProceso);
        model.addAttribute("consultasRespondidas", consultasRespondidas);
        
        // Estadísticas de blog
        try {
            var articulosPublicados = blogService.obtenerArticulosPublicados();
            var destacado = blogService.obtenerArticuloDestacado();
            
            model.addAttribute("totalArticulos", articulosPublicados != null ? articulosPublicados.size() : 0);
            model.addAttribute("tieneDestacado", destacado != null);
        } catch (Exception e) {
            model.addAttribute("totalArticulos", 0);
            model.addAttribute("tieneDestacado", false);
            model.addAttribute("errorBlog", "No se pudo cargar estadísticas del blog");
        }
        
        return "admin/dashboard";
    }
}
