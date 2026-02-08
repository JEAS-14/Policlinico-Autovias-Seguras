package com.policlinico.autovias.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rate_limit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateLimitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String ipAddress; // Soporta IPv4 e IPv6

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "request_type", nullable = false, length = 50)
    private String requestType; // "CONSULTA", "RECLAMACION", etc.

    public RateLimitLog(String ipAddress, String requestType) {
        this.ipAddress = ipAddress;
        this.requestType = requestType;
        this.timestamp = LocalDateTime.now();
    }
}
