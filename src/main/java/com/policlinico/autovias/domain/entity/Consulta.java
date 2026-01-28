package com.policlinico.autovias.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.policlinico.autovias.domain.enums.EstadoConsulta;

@Entity
@Table(name = "consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String numeroTicket;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellido;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false, length = 9)
    private String telefono;
    
    @Column(nullable = false)
    private String tipoConsulta;
    
    private String empresa;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoConsulta estado = EstadoConsulta.PENDIENTE;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaRespuesta;
    
    @Column(columnDefinition = "TEXT")
    private String respuesta;
    
    private String respondidoPor;
    
    private LocalDateTime fechaCitaProgramada;
    
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
    
    // Constructor desde DTO
    public static Consulta fromDTO(com.policlinico.autovias.application.dto.ConsultaDTO dto) {
        Consulta consulta = new Consulta();
        consulta.setNombre(dto.getNombre());
        consulta.setApellido(dto.getApellido());
        consulta.setEmail(dto.getEmail());
        consulta.setTelefono(dto.getTelefono());
        consulta.setTipoConsulta(dto.getTipoConsulta());
        consulta.setEmpresa(dto.getEmpresa());
        consulta.setMensaje(dto.getMensaje());
        consulta.setEstado(EstadoConsulta.PENDIENTE);
        return consulta;
    }
}