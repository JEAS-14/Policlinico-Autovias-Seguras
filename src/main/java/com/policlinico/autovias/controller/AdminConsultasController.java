package com.policlinico.autovias.controller;

import com.policlinico.autovias.model.dto.RespuestaDTO;
import com.policlinico.autovias.model.entity.Consulta;
import com.policlinico.autovias.model.entity.EstadoConsulta;
import com.policlinico.autovias.service.ConsultaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/consultas")
@RequiredArgsConstructor
public class AdminConsultasController {
    
    private final ConsultaService consultaService;
    
    @GetMapping
    public String listarConsultas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) EstadoConsulta estado,
            @RequestParam(required = false) String busqueda,
            HttpSession session,
            Model model) {
        
        // Añadir nombre de usuario al modelo
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        PageRequest pageRequest = PageRequest.of(page, 10, 
            Sort.by("fechaCreacion").descending());
        
        Page<Consulta> consultas;
        
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            consultas = consultaService.buscarConsultas(busqueda, pageRequest);
        } else if (estado != null) {
            consultas = consultaService.obtenerConsultasPorEstado(estado, pageRequest);
        } else {
            consultas = consultaService.obtenerTodas(pageRequest);
        }
        
        model.addAttribute("consultas", consultas);
        model.addAttribute("estadoActual", estado);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estados", EstadoConsulta.values());
        
        // Contador de consultas por estado
        model.addAttribute("pendientes", consultaService.contarPorEstado(EstadoConsulta.PENDIENTE));
        model.addAttribute("enProceso", consultaService.contarPorEstado(EstadoConsulta.EN_PROCESO));
        model.addAttribute("respondidas", consultaService.contarPorEstado(EstadoConsulta.RESPONDIDA));
        model.addAttribute("citasProgramadas", consultaService.contarPorEstado(EstadoConsulta.CITA_PROGRAMADA));
        
        return "admin/consultas/listado";
    }
    
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, HttpSession session, Model model) {
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        Consulta consulta = consultaService.obtenerPorId(id);
        model.addAttribute("consulta", consulta);
        model.addAttribute("respuestaDTO", new RespuestaDTO());
        model.addAttribute("estados", EstadoConsulta.values());
        return "admin/consultas/detalle";
    }
    
    @PostMapping("/{id}/responder")
    public String responderConsulta(
            @PathVariable Long id,
            @Valid @ModelAttribute RespuestaDTO respuestaDTO,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", 
                "Error en la validación de la respuesta");
            return "redirect:/admin/consultas/" + id;
        }
        
        try {
            String usuario = (String) session.getAttribute("nombreUsuario");
            consultaService.responderConsulta(id, respuestaDTO, usuario);
            
            redirectAttributes.addFlashAttribute("success", 
                "Respuesta enviada exitosamente al cliente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al enviar la respuesta: " + e.getMessage());
        }
        
        return "redirect:/admin/consultas";
    }
    
    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoConsulta nuevoEstado,
            RedirectAttributes redirectAttributes) {
        
        try {
            consultaService.cambiarEstado(id, nuevoEstado);
            redirectAttributes.addFlashAttribute("success", 
                "Estado actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al cambiar el estado: " + e.getMessage());
        }
        
        return "redirect:/admin/consultas/" + id;
    }
}