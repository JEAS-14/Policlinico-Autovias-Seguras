package com.policlinico.autovias.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.policlinico.autovias.domain.entity.RateLimitLog;
import com.policlinico.autovias.domain.repository.RateLimitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RateLimitRepository rateLimitRepository;

    @Value("${app.rate-limit.max-requests:5}")
    private int maxRequestsPerDay; // Default: 5 requests per day

    @Value("${app.rate-limit.window-hours:24}")
    private int windowHours; // Default: 24 hours

    /**
     * Verifica si la IP ha excedido el límite de solicitudes.
     * 
     * @param ipAddress Dirección IP del cliente
     * @param requestType Tipo de solicitud (CONSULTA, RECLAMACION, etc.)
     * @return true si se permite la solicitud, false si está bloqueada
     */
    public boolean isRequestAllowed(String ipAddress, String requestType) {
        LocalDateTime since = LocalDateTime.now().minusHours(windowHours);

        List<RateLimitLog> recentRequests = rateLimitRepository
                .findByIpAddressAndRequestTypeAndTimestampAfter(ipAddress, requestType, since);

        return recentRequests.size() < maxRequestsPerDay;
    }

    /**
     * Registra una solicitud de una IP.
     * 
     * @param ipAddress Dirección IP del cliente
     * @param requestType Tipo de solicitud
     */
    public void recordRequest(String ipAddress, String requestType) {
        RateLimitLog log = new RateLimitLog(ipAddress, requestType);
        rateLimitRepository.save(log);
    }

    /**
     * Obtiene el número de solicitudes restantes para una IP.
     * 
     * @param ipAddress Dirección IP del cliente
     * @param requestType Tipo de solicitud
     * @return Número de solicitudes permitidas restantes (puede ser negativo)
     */
    public int getRemainingRequests(String ipAddress, String requestType) {
        LocalDateTime since = LocalDateTime.now().minusHours(windowHours);

        List<RateLimitLog> recentRequests = rateLimitRepository
                .findByIpAddressAndRequestTypeAndTimestampAfter(ipAddress, requestType, since);

        return maxRequestsPerDay - recentRequests.size();
    }

    /**
     * Limpia logs antiguos (más de 30 días) para mantener la BD limpia.
     */
    public void cleanupOldLogs() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        rateLimitRepository.deleteByTimestampBefore(thirtyDaysAgo);
    }
}
