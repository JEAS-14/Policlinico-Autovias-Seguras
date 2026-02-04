package com.policlinico.autovias.application.service;

import com.policlinico.autovias.application.dto.ConsultaDTO;
import com.policlinico.autovias.application.dto.RespuestaDTO;
import com.policlinico.autovias.domain.entity.Consulta;
import com.policlinico.autovias.domain.enums.EstadoConsulta;
import com.policlinico.autovias.infrastructure.persitence.repository.ConsultaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultaService {
    
    private final ConsultaRepository consultaRepository;
    private final EmailService emailService;
    private final GoogleSheetsService googleSheetsService;
    
    @Transactional
    public Consulta crearConsulta(ConsultaDTO consultaDTO) {
        // Convertir DTO a Entity
        Consulta consulta = Consulta.fromDTO(consultaDTO);
        consulta.setNumeroTicket(generarNumeroTicket());
        consulta.setFechaCreacion(LocalDateTime.now()); // Asegurar que se asigne la fecha
        
        // Guardar en base de datos
        consulta = consultaRepository.save(consulta);
        log.info("Consulta creada con ticket: {}", consulta.getNumeroTicket());
        
        // Guardar en Google Sheets
        try {
            googleSheetsService.guardarConsulta(
                    consultaDTO.getNombre(),
                    consultaDTO.getApellido(),
                    consultaDTO.getEmail(),
                    consultaDTO.getTelefono(),
                    consultaDTO.getTipoConsulta(),
                    consultaDTO.getMensaje(),
                    consulta.getNumeroTicket()
            );
        } catch (Exception e) {
            log.error("Error al guardar consulta en Google Sheets", e);
        }
        
        // Enviar emails
        try {
            emailService.enviarNotificacionConsulta(consultaDTO, consulta.getNumeroTicket());
            emailService.enviarConfirmacionCliente(consultaDTO, consulta.getNumeroTicket());
        } catch (Exception e) {
            log.error("Error al enviar emails para consulta {}", consulta.getNumeroTicket(), e);
            // No lanzamos excepción para no interrumpir el proceso
        }
        
        return consulta;
    }
    
    @Transactional
    public Consulta responderConsulta(Long id, RespuestaDTO respuestaDTO, String usuario) {
        Consulta consulta = consultaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        consulta.setRespuesta(respuestaDTO.getRespuesta());
        consulta.setRespondidoPor(usuario);
        consulta.setFechaRespuesta(LocalDateTime.now());
        
        if (respuestaDTO.isProgramarCita() && respuestaDTO.getFechaCita() != null) {
            consulta.setFechaCitaProgramada(respuestaDTO.getFechaCita());
            consulta.setEstado(EstadoConsulta.CITA_PROGRAMADA);
        } else {
            consulta.setEstado(EstadoConsulta.RESPONDIDA);
        }
        
        consulta = consultaRepository.save(consulta);
        log.info("Consulta {} respondida por {}", consulta.getId(), usuario);
        
        // Enviar respuesta al cliente
        try {
            emailService.enviarRespuestaCliente(consulta);
        } catch (Exception e) {
            log.error("Error al enviar respuesta al cliente para consulta {}", 
                consulta.getId(), e);
            throw new RuntimeException("Error al enviar respuesta por email", e);
        }
        
        return consulta;
    }
    
    @Transactional
    public Consulta cambiarEstado(Long id, EstadoConsulta nuevoEstado) {
        Consulta consulta = consultaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        consulta.setEstado(nuevoEstado);
        return consultaRepository.save(consulta);
    }
    
    public Page<Consulta> obtenerConsultasPorEstado(EstadoConsulta estado, Pageable pageable) {
        return consultaRepository.findByEstado(estado, pageable);
    }
    
    public Page<Consulta> obtenerTodas(Pageable pageable) {
        return consultaRepository.findAll(pageable);
    }
    
    public Page<Consulta> buscarConsultas(String termino, Pageable pageable) {
        if (termino == null || termino.trim().isEmpty()) {
            return consultaRepository.findAll(pageable);
        }
        return consultaRepository.buscarPorTermino(termino, pageable);
    }
    
    public Consulta obtenerPorId(Long id) {
        return consultaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
    }
    
    public Consulta obtenerPorTicket(String numeroTicket) {
        return consultaRepository.findByNumeroTicket(numeroTicket)
            .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
    }
    
    public long contarTodas() {
        return consultaRepository.count();
    }
    
    public long contarPorEstado(EstadoConsulta estado) {
        return consultaRepository.countByEstado(estado);
    }
    
    public long contarConsultasHoy() {
        return consultaRepository.countConsultasHoy();
    }
    
    @Transactional
    public void eliminarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consulta no encontrada"));
        
        log.info("Eliminando consulta con id: {}", consulta.getId());
        consultaRepository.deleteById(id);
    }
    
    private String generarNumeroTicket() {
        LocalDateTime now = LocalDateTime.now();
        String fecha = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = consultaRepository.count() + 1;
        return String.format("CON-%s-%05d", fecha, count);
    }
}