package com.policlinico.autovias.application.service;

import com.policlinico.autovias.application.dto.ArticuloBlogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogService {

    private final GoogleSheetsService googleSheetsService;
    
    // Caché simple en memoria
    private List<ArticuloBlogDTO> cachedArticulos = null;
    private LocalDateTime ultimaActualizacion = null;
    private static final long CACHE_DURACION_MINUTOS = 5;

    /**
     * Obtiene todos los artículos publicados ordenados por ID descendente (más recientes primero)
     */
    public List<ArticuloBlogDTO> obtenerArticulosPublicados() {
        // Verificar si el caché es válido
        if (cachedArticulos != null && ultimaActualizacion != null) {
            long minutosDesdeActualizacion = java.time.Duration.between(ultimaActualizacion, LocalDateTime.now()).toMinutes();
            if (minutosDesdeActualizacion < CACHE_DURACION_MINUTOS) {
                log.info("Usando artículos desde caché ({} minutos)", minutosDesdeActualizacion);
                return new ArrayList<>(cachedArticulos);
            }
        }
        
        log.info("Actualizando caché de artículos...");
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
            
            // Ordenar por fecha descendente (más recientes primero)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            resultado.sort((a, b) -> {
                try {
                    LocalDate fechaA = LocalDate.parse(a.getFecha(), formatter);
                    LocalDate fechaB = LocalDate.parse(b.getFecha(), formatter);
                    return fechaB.compareTo(fechaA); // Descendente (más reciente primero)
                } catch (Exception e) {
                    log.warn("Error al parsear fechas, usando orden original");
                    return 0;
                }
            });
            
            log.info("=== RESULTADO FINAL: {} artículos publicados de {} totales ===", 
                     resultado.size(), data.size() - 1);
            
            // Actualizar caché
            cachedArticulos = new ArrayList<>(resultado);
            ultimaActualizacion = LocalDateTime.now();
            log.info("Caché actualizado. Válido por {} minutos", CACHE_DURACION_MINUTOS);
            
            return resultado;
                    
        } catch (Exception e) {
            log.error("Error al obtener artículos publicados", e);
            // Si hay error pero tenemos caché, devolver el caché aunque esté expirado
            if (cachedArticulos != null) {
                log.warn("Usando caché expirado por error en Google Sheets");
                return new ArrayList<>(cachedArticulos);
            }
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
