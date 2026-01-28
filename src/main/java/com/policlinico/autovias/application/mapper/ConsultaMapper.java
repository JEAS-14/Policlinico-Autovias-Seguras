package com.policlinico.autovias.application.mapper;

import org.springframework.stereotype.Component;

import com.policlinico.autovias.application.dto.ConsultaDTO;
import com.policlinico.autovias.domain.entity.Consulta;

@Component

public class ConsultaMapper {

    public Consulta toEntity(ConsultaDTO dto) {
        if (dto == null) return null;
        
        Consulta consulta = new Consulta();
        consulta.setNombre(dto.getNombre());
        consulta.setApellido(dto.getApellido());
        consulta.setEmail(dto.getEmail());
        consulta.setTelefono(dto.getTelefono());
        consulta.setTipoConsulta(dto.getTipoConsulta());
        consulta.setEmpresa(dto.getEmpresa());
        consulta.setMensaje(dto.getMensaje());
        return consulta;
    }

    public ConsultaDTO toDTO(Consulta entity) {
        // Por si necesitas devolver datos al frontend
        ConsultaDTO dto = new ConsultaDTO();
        dto.setNombre(entity.getNombre());
        // ... setear el resto
        return dto;
    }
}