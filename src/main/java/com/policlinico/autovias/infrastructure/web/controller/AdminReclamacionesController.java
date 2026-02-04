package com.policlinico.autovias.infrastructure.web.controller;

import com.policlinico.autovias.application.service.EmailService;
import com.policlinico.autovias.application.service.GoogleSheetsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/reclamaciones")
@RequiredArgsConstructor
@Slf4j
public class AdminReclamacionesController {
    
    private final GoogleSheetsService googleSheetsService;
    private final EmailService emailService;
    
    @GetMapping
    public String listarReclamaciones(
            @RequestParam(defaultValue = "0") int page,
            HttpSession session,
            Model model) {
        
        // Obtener nombre de usuario
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        try {
            // Leer todas las reclamaciones desde Google Sheets
            List<List<Object>> reclamaciones = googleSheetsService.leerReclamaciones();
            
            // Calcular paginación manual
            int pageSize = 20;
            int totalReclamaciones = reclamaciones.size() > 0 ? reclamaciones.size() - 1 : 0; // Menos header
            int totalPages = (int) Math.ceil((double) totalReclamaciones / pageSize);

            // Contadores por estado
            int pendientes = 0;
            int enRevision = 0;
            int resueltas = 0;
            int cerradas = 0;
            for (int i = 1; i < reclamaciones.size(); i++) {
                List<Object> fila = reclamaciones.get(i);
                String estado = (fila.size() > 11 && fila.get(11) != null) ? String.valueOf(fila.get(11)).trim().toUpperCase() : "PENDIENTE";
                switch (estado) {
                    case "PENDIENTE" -> pendientes++;
                    case "EN_REVISION" -> enRevision++;
                    case "RESUELTA" -> resueltas++;
                    case "CERRADA" -> cerradas++;
                    default -> pendientes++;
                }
            }
            
            // Obtener página actual
            int start = page * pageSize + 1; // +1 para saltar el header
            int end = Math.min(start + pageSize, reclamaciones.size());
            
            List<List<Object>> paginaActual = reclamaciones.size() > start ? 
                    reclamaciones.subList(start, end) : List.of();
            
            model.addAttribute("reclamaciones", reclamaciones);
            model.addAttribute("totalReclamaciones", totalReclamaciones);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("reclPendientes", pendientes);
            model.addAttribute("reclEnRevision", enRevision);
            model.addAttribute("reclResueltas", resueltas);
            model.addAttribute("reclCerradas", cerradas);
            
        } catch (Exception e) {
            log.error("Error al obtener reclamaciones", e);
            model.addAttribute("error", "No se pudieron cargar las reclamaciones: " + e.getMessage());
            model.addAttribute("reclamaciones", List.of());
        }
        
        return "admin/reclamaciones";
    }

    @GetMapping("/detalle")
    public String verDetalleReclamacion(
            @RequestParam String ticket,
            HttpSession session,
            Model model) {

        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));

        try {
                List<Object> reclamacion = googleSheetsService.buscarReclamacionPorTicket(ticket);

            if (reclamacion == null) {
                model.addAttribute("error", "No se encontró la reclamación con ticket: " + ticket);
                return "admin/reclamaciones";
            }

            String estadoActual = reclamacion.size() > 11 ? String.valueOf(reclamacion.get(11)) : "PENDIENTE";
            String respuestaActual = reclamacion.size() > 12 ? String.valueOf(reclamacion.get(12)) : "";

            model.addAttribute("reclamacion", reclamacion);
            model.addAttribute("estadoActual", estadoActual);
            model.addAttribute("respuestaActual", respuestaActual);
            model.addAttribute("ticket", ticket);

            return "admin/reclamaciones/detalle";

        } catch (Exception e) {
            log.error("Error al obtener detalle de reclamación", e);
            model.addAttribute("error", "No se pudo cargar el detalle: " + e.getMessage());
            return "admin/reclamaciones";
        }
    }

    @PostMapping("/responder")
        public String responderReclamacion(
            @RequestParam String ticket,
            @RequestParam String estado,
            @RequestParam(required = false) String respuesta,
            @RequestParam(name = "accion", defaultValue = "guardarYEnviar") String accion,
            HttpSession session,
            Model model) {

        try {
            String respondidoPor = String.valueOf(session.getAttribute("nombreUsuario"));
            googleSheetsService.actualizarReclamacion(ticket, estado, respuesta, respondidoPor);

            if ("guardarYEnviar".equalsIgnoreCase(accion)) {
                List<Object> reclamacion = googleSheetsService.buscarReclamacionPorTicket(ticket);
                if (reclamacion != null && reclamacion.size() > 4) {
                    String email = String.valueOf(reclamacion.get(4));
                    String nombre = reclamacion.size() > 1 ? String.valueOf(reclamacion.get(1)) : "";
                    String apellido = reclamacion.size() > 2 ? String.valueOf(reclamacion.get(2)) : "";
                    emailService.enviarRespuestaReclamacion(email, nombre, apellido, ticket, estado, respuesta);
                }
            }

            return "redirect:/admin/reclamaciones/detalle?ticket=" + ticket + "&ok";
        } catch (Exception e) {
            log.error("Error al responder reclamación", e);
            model.addAttribute("error", "No se pudo guardar la respuesta: " + e.getMessage());
            return "redirect:/admin/reclamaciones/detalle?ticket=" + ticket + "&error";
        }
    }
}
