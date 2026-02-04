package com.policlinico.autovias.config;

import com.policlinico.autovias.application.service.ConsultaService;
import com.policlinico.autovias.application.service.GoogleSheetsService;
import com.policlinico.autovias.domain.enums.EstadoConsulta;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {
    
    private final ConsultaService consultaService;
    private final GoogleSheetsService googleSheetsService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        
        // Verificar si el usuario está autenticado
        if (session.getAttribute("usuarioAutenticado") == null) {
            response.sendRedirect("/admin/login");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Agregar contadores de notificaciones al modelo si existe
        if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
            try {
                long consultasPendientes = consultaService.contarPorEstado(EstadoConsulta.PENDIENTE);
                modelAndView.addObject("consultasPendientes", consultasPendientes);
                
                try {
                    long pendientes = 0L;
                    var values = googleSheetsService.leerReclamaciones();
                    for (int i = 1; i < (values != null ? values.size() : 0); i++) {
                        var fila = values.get(i);

                        String estadoRaw = (fila.size() > 11 && fila.get(11) != null)
                                ? String.valueOf(fila.get(11)).trim().toUpperCase()
                                : "";

                        if (estadoRaw.startsWith("REC") || estadoRaw.isBlank()) {
                            estadoRaw = (fila.size() > 12 && fila.get(12) != null)
                                    ? String.valueOf(fila.get(12)).trim().toUpperCase()
                                    : "PENDIENTE";
                        }

                        String estado = estadoRaw.isBlank() ? "PENDIENTE" : estadoRaw;
                        if ("PENDIENTE".equals(estado)) pendientes++;
                    }
                    modelAndView.addObject("reclamacionesPendientes", pendientes);
                } catch (Exception ignored) {
                    modelAndView.addObject("reclamacionesPendientes", 0L);
                }
            } catch (Exception e) {
                // Si falla, no bloquear la vista
            }
        }
    }
}