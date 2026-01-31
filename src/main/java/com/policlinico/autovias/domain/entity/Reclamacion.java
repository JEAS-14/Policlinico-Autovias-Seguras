package com.policlinico.autovias.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reclamaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reclamacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String numeroTicket;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellido;
    
    @Column(nullable = false, length = 12)
    private String dniCe;
    
    @Column(nullable = false)
    private String domicilio;
    
    @Column(nullable = false, length = 9)
    private String telefono;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false, length = 10)
    private String tipo; // RECLAMO o QUEJA
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String detalle;
    
    @Column(columnDefinition = "TEXT")
    private String pedido;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaRespuesta;
    
    @Column(columnDefinition = "TEXT")
    private String respuesta;
    
    private String respondidoPor;
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}
