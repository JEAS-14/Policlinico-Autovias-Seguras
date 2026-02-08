package com.policlinico.autovias.infrastructure.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    /**
     * Extrae la dirección IP real del cliente de una solicitud HTTP.
     * Considera proxies y load balancers.
     * 
     * @param request HttpServletRequest de Spring
     * @return Dirección IP del cliente
     */
    public static String getClientIp(HttpServletRequest request) {
        // Comprueba headers de proxy
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // Si hay múltiples IPs (proxy chain), toma la primera
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("Cf-Connecting-Ip"); // Cloudflare
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // Si no hay header de proxy, usa la IP remota directa
        return request.getRemoteAddr();
    }
}
