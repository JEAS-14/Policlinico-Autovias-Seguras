package com.policlinico.autovias.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.policlinico.autovias.domain.entity.RateLimitLog;

@Repository
public interface RateLimitRepository extends JpaRepository<RateLimitLog, Long> {

    // Obtener todos los logs de una IP en las últimas 24 horas
    List<RateLimitLog> findByIpAddressAndRequestTypeAndTimestampAfter(
            String ipAddress,
            String requestType,
            LocalDateTime since);

    // Limpiar logs antiguos (más de 30 días)
    void deleteByTimestampBefore(LocalDateTime before);
}
