package com.policlinico.autovias.application.service;

import com.policlinico.autovias.application.dto.ReclamacionDTO;
import com.policlinico.autovias.domain.entity.Reclamacion;
import com.policlinico.autovias.domain.repository.ReclamacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReclamacionService {
    
    private final ReclamacionRepository reclamacionRepository;
    private final EmailService emailService;
    private final GoogleSheetsService googleSheetsService;
    
    @Transactional
    public Reclamacion crearReclamacion(ReclamacionDTO reclamacionDTO) {
        // Convertir DTO a Entity
        Reclamacion reclamacion = new Reclamacion();
        reclamacion.setNumeroTicket(generarNumeroTicket());
        reclamacion.setNombre(reclamacionDTO.getNombre());
        reclamacion.setApellido(reclamacionDTO.getApellido());
        reclamacion.setDniCe(reclamacionDTO.getDniCe());
        reclamacion.setDomicilio(reclamacionDTO.getDomicilio());
        reclamacion.setTelefono(reclamacionDTO.getTelefono());
        reclamacion.setEmail(reclamacionDTO.getEmail());
        reclamacion.setTipo(reclamacionDTO.getTipo());
        reclamacion.setDetalle(reclamacionDTO.getDetalle());
        reclamacion.setPedido(reclamacionDTO.getPedido());
        
        // Guardar en base de datos
        reclamacion = reclamacionRepository.save(reclamacion);
        log.info("Reclamación creada con ticket: {}", reclamacion.getNumeroTicket());
        
        // Guardar en Google Sheets (backup)
        try {
            googleSheetsService.guardarReclamacion(
                    reclamacionDTO.getNombre(),
                    reclamacionDTO.getApellido(),
                    reclamacionDTO.getDniCe(),
                    reclamacionDTO.getEmail(),
                    reclamacionDTO.getTelefono(),
                    reclamacionDTO.getDomicilio(),
                    reclamacionDTO.getTipo(),
                    reclamacionDTO.getDetalle(),
                    reclamacionDTO.getPedido(),
                    reclamacion.getNumeroTicket()
            );
        } catch (Exception e) {
            log.error("Error al guardar reclamación en Google Sheets (backup)", e);
            // No lanzamos excepción, el backup no debe interrumpir el proceso
        }
        
        // Enviar emails
        try {
            emailService.enviarNotificacionReclamacion(reclamacionDTO);
            emailService.enviarConfirmacionReclamante(reclamacionDTO);
        } catch (Exception e) {
            log.error("Error al enviar emails para reclamación {}", reclamacion.getNumeroTicket(), e);
            // No lanzamos excepción para no interrumpir el proceso
        }
        
        return reclamacion;
    }
    
    private String generarNumeroTicket() {
        return "REC-" + System.currentTimeMillis();
    }
}
