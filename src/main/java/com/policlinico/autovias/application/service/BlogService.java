package com.policlinico.autovias.application.service;

import com.policlinico.autovias.application.dto.ArticuloBlogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogService {

    private final GoogleSheetsService googleSheetsService;

    /**
     * Obtiene todos los artículos publicados ordenados por ID descendente (más recientes primero)
     */
    public List<ArticuloBlogDTO> obtenerArticulosPublicados() {
        try {
            List<List<Object>> data = googleSheetsService.leerArticulosBlog();
            
            if (data == null || data.isEmpty()) {
                log.warn("No hay artículos en Google Sheets");
                return new ArrayList<>();
            }
            
            log.info("Leyendo {} filas de Google Sheets", data.size());
            
            // Saltar la fila de encabezados (índice 0)
            List<ArticuloBlogDTO> resultado = new ArrayList<>();
            for (int i = 1; i < data.size(); i++) {
                List<Object> row = data.get(i);
                
                log.debug("Fila {}: Tamaño={}, Contenido={}", i, row.size(), row);
                
                // Validar que la fila tenga suficientes columnas (al menos hasta Estado)
                if (row.size() < 9) {
                    log.debug("Fila {} ignorada: solo tiene {} columnas", i, row.size());
                    continue;
                }
                
                // Obtener estado (columna I, índice 8)
                String estadoRaw = getString(row, 8);
                String estado = estadoRaw.toUpperCase().trim();
                
                // Obtener ID y título para logging
                String id = getString(row, 0);
                String titulo = getString(row, 1);
                
                log.info("Artículo {} - ID: '{}', Título: '{}', Estado raw: '{}', Estado normalizado: '{}'", 
                         i, id, titulo, estadoRaw, estado);
                
                // Filtrar solo artículos PUBLICADOS
                if ("PUBLICADO".equals(estado)) {
                    ArticuloBlogDTO articulo = convertirAArticulo(row);
                    resultado.add(articulo);
                    log.info("✓ Artículo PUBLICADO agregado: ID={}, Título={}", id, titulo);
                } else {
                    log.info("✗ Artículo rechazado: ID={}, Estado='{}' (no es 'PUBLICADO')", id, estado);
                }
            }
            
            // Ordenar por ID descendente (más recientes primero)
            resultado.sort((a, b) -> b.getId().compareTo(a.getId()));
            
            log.info("=== RESULTADO FINAL: {} artículos publicados de {} totales ===", 
                     resultado.size(), data.size() - 1);
            return resultado;
                    
        } catch (Exception e) {
            log.error("Error al obtener artículos publicados", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene el artículo destacado (el primero marcado como SI en Destacado)
     */
    public ArticuloBlogDTO obtenerArticuloDestacado() {
        return obtenerArticulosPublicados().stream()
                .filter(ArticuloBlogDTO::getDestacado)
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene los artículos normales (no destacados)
     */
    public List<ArticuloBlogDTO> obtenerArticulosNormales() {
        return obtenerArticulosPublicados().stream()
                .filter(a -> !a.getDestacado())
                .collect(Collectors.toList());
    }

    /**
     * Convierte una fila de Google Sheets a ArticuloBlogDTO
     */
    private ArticuloBlogDTO convertirAArticulo(List<Object> row) {
        return ArticuloBlogDTO.builder()
                .id(getInteger(row, 0))
                .titulo(getString(row, 1))
                .resumen(getString(row, 2))
                .contenido(getString(row, 3))
                .fecha(getString(row, 4))
                .categoria(getString(row, 5))
                .imagen(getString(row, 6))
                .destacado("SI".equalsIgnoreCase(getString(row, 7)))
                .estado(getString(row, 8))
                .autor(getString(row, 9))
                .build();
    }

    /**
     * Obtiene un valor String de una fila, retorna "" si no existe
     */
    private String getString(List<Object> row, int index) {
        if (row.size() > index && row.get(index) != null) {
            return row.get(index).toString().trim();
        }
        return "";
    }

    /**
     * Obtiene un valor Integer de una fila, retorna 0 si no existe o es inválido
     */
    private Integer getInteger(List<Object> row, int index) {
        try {
            String value = getString(row, index);
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("No se pudo convertir '{}' a Integer", getString(row, index));
            return 0;
        }
    }
}
