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
@RequestMapping("/admin/consultas")
@RequiredArgsConstructor
@Slf4j
public class AdminConsultasController {
    
    private final GoogleSheetsService googleSheetsService;
    private final EmailService emailService;
    
    @GetMapping
    public String listarConsultas(
            @RequestParam(defaultValue = "0") int page,
            HttpSession session,
            Model model) {
        
        // Obtener nombre de usuario
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        try {
            // Leer todas las consultas desde Google Sheets
            List<List<Object>> consultas = googleSheetsService.leerConsultas();
            
            // Filtrar filas vacías y calcular contadores
            List<List<Object>> consultasFiltradas = new java.util.ArrayList<>();
            int pendientes = 0;
            int enProceso = 0;
            int respondidas = 0;
            int citasProgramadas = 0;
            for (int i = 1; i < consultas.size(); i++) {
                List<Object> fila = consultas.get(i);
                if (fila == null || fila.isEmpty() || fila.get(0) == null) {
                    continue;
                }
                consultasFiltradas.add(fila);
                String estado = (fila.size() > 7 && fila.get(7) != null)
                        ? String.valueOf(fila.get(7)).trim().toUpperCase()
                        : "PENDIENTE";
                switch (estado) {
                    case "PENDIENTE" -> pendientes++;
                    case "EN_PROCESO" -> enProceso++;
                    case "RESPONDIDA" -> respondidas++;
                    case "CITA_PROGRAMADA" -> citasProgramadas++;
                    default -> pendientes++;
                }
            }

            // Calcular paginación manual con filas filtradas
            int pageSize = 20;
            int totalConsultas = consultasFiltradas.size();
            int totalPages = (int) Math.ceil((double) totalConsultas / pageSize);

            int start = page * pageSize;
            int end = Math.min(start + pageSize, totalConsultas);
            List<List<Object>> paginaActual = totalConsultas > start
                    ? consultasFiltradas.subList(start, end)
                    : List.of();

            model.addAttribute("consultas", paginaActual);
            model.addAttribute("totalConsultas", totalConsultas);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pendientes", pendientes);
            model.addAttribute("enProceso", enProceso);
            model.addAttribute("respondidas", respondidas);
            model.addAttribute("citasProgramadas", citasProgramadas);
            
        } catch (Exception e) {
            log.error("Error al obtener consultas", e);
            model.addAttribute("error", "No se pudieron cargar las consultas: " + e.getMessage());
            model.addAttribute("consultas", List.of());
        }
        
        return "admin/consultas/listado";
    }

    @GetMapping("/detalle")
    public String verDetalleConsulta(
            @RequestParam String ticket,
            HttpSession session,
            Model model) {

        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));

        try {
            List<Object> consulta = googleSheetsService.buscarConsultaPorTicket(ticket);

            if (consulta == null) {
                model.addAttribute("error", "No se encontró la consulta con ticket: " + ticket);
                return "admin/consultas/listado";
            }

            String estadoActual = consulta.size() > 7 ? String.valueOf(consulta.get(7)) : "PENDIENTE";
            String respuestaActual = consulta.size() > 9 ? String.valueOf(consulta.get(9)) : "";

            model.addAttribute("consulta", consulta);
            model.addAttribute("estadoActual", estadoActual);
            model.addAttribute("respuestaActual", respuestaActual);
            model.addAttribute("ticket", ticket);

            return "admin/consultas/detalle";

        } catch (Exception e) {
            log.error("Error al obtener detalle de consulta", e);
            model.addAttribute("error", "No se pudo cargar el detalle: " + e.getMessage());
            return "admin/consultas/listado";
        }
    }

    @PostMapping("/responder")
    public String responderConsulta(
            @RequestParam String ticket,
            @RequestParam String estado,
            @RequestParam(required = false) String respuesta,
            @RequestParam(name = "accion", defaultValue = "guardarYEnviar") String accion,
            HttpSession session,
            Model model) {

        try {
            String respondidoPor = String.valueOf(session.getAttribute("nombreUsuario"));
            googleSheetsService.actualizarConsulta(ticket, estado, respuesta, respondidoPor);

            if ("guardarYEnviar".equalsIgnoreCase(accion)) {
                List<Object> consulta = googleSheetsService.buscarConsultaPorTicket(ticket);
                if (consulta != null && consulta.size() > 4) {
                    String email = String.valueOf(consulta.get(3));
                    String nombre = consulta.size() > 1 ? String.valueOf(consulta.get(1)) : "";
                    String apellido = consulta.size() > 2 ? String.valueOf(consulta.get(2)) : "";
                    emailService.enviarRespuestaConsultaCliente(email, nombre, apellido, ticket, estado, respuesta);
                }
            }

            return "redirect:/admin/consultas/detalle?ticket=" + ticket + "&ok";
        } catch (Exception e) {
            log.error("Error al responder consulta", e);
            model.addAttribute("error", "No se pudo guardar la respuesta: " + e.getMessage());
            return "redirect:/admin/consultas/detalle?ticket=" + ticket + "&error";
        }
    }

    @PostMapping("/eliminar")
    public String eliminarConsulta(
            @RequestParam String ticket,
            HttpSession session,
            Model model) {

        try {
            googleSheetsService.eliminarConsulta(ticket);
            return "redirect:/admin/consultas?eliminada";
        } catch (Exception e) {
            log.error("Error al eliminar consulta", e);
            model.addAttribute("error", "No se pudo eliminar la consulta: " + e.getMessage());
            return "redirect:/admin/consultas/detalle?ticket=" + ticket + "&error";
        }
    }
}