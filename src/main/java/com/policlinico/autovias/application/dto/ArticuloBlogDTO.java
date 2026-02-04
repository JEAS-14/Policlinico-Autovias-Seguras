package com.policlinico.autovias.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloBlogDTO {
    private Integer id;
    private String titulo;
    private String resumen;
    private String contenido;
    private String fecha;
    private String categoria;
    private String imagen;
    private Boolean destacado;
    private String estado;
    private String autor;
}
