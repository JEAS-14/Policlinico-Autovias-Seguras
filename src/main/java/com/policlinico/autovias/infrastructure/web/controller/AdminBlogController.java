package com.policlinico.autovias.infrastructure.web.controller;

import com.policlinico.autovias.application.dto.ArticuloBlogDTO;
import com.policlinico.autovias.application.service.BlogService;
import com.policlinico.autovias.application.service.GoogleSheetsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar el blog desde el panel de administración
 */
@Controller
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
@Slf4j
public class AdminBlogController {

    private final BlogService blogService;
    private final GoogleSheetsService googleSheetsService;

    /**
     * Muestra la vista de gestión del blog (previsualización y estadísticas)
     */
    @GetMapping
    public String gestionarBlog(Model model, HttpSession session) {
        // Agregar nombre de usuario
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));

        try {
            // Obtener todos los artículos publicados
            List<ArticuloBlogDTO> articulosPublicados = blogService.obtenerArticulosPublicados();
            
            // Obtener el destacado
            ArticuloBlogDTO destacado = blogService.obtenerArticuloDestacado();
            
            // Obtener artículos normales
            List<ArticuloBlogDTO> articulos = blogService.obtenerArticulosNormales();
            
            // Estadísticas
            model.addAttribute("totalArticulos", articulosPublicados.size());
            model.addAttribute("destacado", destacado);
            model.addAttribute("articulos", articulos);
            model.addAttribute("tieneDestacado", destacado != null);

                // Cargar todos los artículos para gestión rápida en el dashboard
                List<List<Object>> articulosSheets = googleSheetsService.leerArticulosBlog();
                if (articulosSheets == null) {
                    articulosSheets = List.of();
                }

                List<ArticuloBlogDTO> articulosGestion = articulosSheets.stream()
                    .skip(1)
                    .filter(fila -> fila.size() >= 9)
                    .map(this::mapToArticuloDTO)
                    .toList();

                long totalArticulosGestion = articulosGestion.size();
                long articulosPublicadosGestion = articulosGestion.stream()
                    .filter(a -> "PUBLICADO".equalsIgnoreCase(a.getEstado()))
                    .count();
                long articulosBorradoresGestion = articulosGestion.stream()
                    .filter(a -> "BORRADOR".equalsIgnoreCase(a.getEstado()))
                    .count();
                long articulosDestacadosGestion = articulosGestion.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getDestacado()))
                    .count();

                model.addAttribute("articulosGestion", articulosGestion);
                model.addAttribute("totalArticulosGestion", totalArticulosGestion);
                model.addAttribute("articulosPublicadosGestion", articulosPublicadosGestion);
                model.addAttribute("articulosBorradoresGestion", articulosBorradoresGestion);
                model.addAttribute("articulosDestacadosGestion", articulosDestacadosGestion);
            
            log.info("Panel de blog cargado: {} artículos totales", articulosPublicados.size());
            
        } catch (Exception e) {
            log.error("Error al cargar panel de blog", e);
            model.addAttribute("error", "Error al cargar los artículos. Verifica la configuración de Google Sheets.");
            model.addAttribute("articulosGestion", List.of());
            model.addAttribute("totalArticulosGestion", 0);
            model.addAttribute("articulosPublicadosGestion", 0);
            model.addAttribute("articulosBorradoresGestion", 0);
            model.addAttribute("articulosDestacadosGestion", 0);
        }

        return "admin/blog";
    }

    private ArticuloBlogDTO mapToArticuloDTO(List<Object> fila) {
        ArticuloBlogDTO dto = new ArticuloBlogDTO();

        if (fila.get(0) != null) {
            try {
                dto.setId(Integer.parseInt(String.valueOf(fila.get(0))));
            } catch (NumberFormatException e) {
                dto.setId(null);
            }
        }

        dto.setTitulo(fila.size() > 1 && fila.get(1) != null ? String.valueOf(fila.get(1)) : "");
        dto.setResumen(fila.size() > 2 && fila.get(2) != null ? String.valueOf(fila.get(2)) : "");
        dto.setContenido(fila.size() > 3 && fila.get(3) != null ? String.valueOf(fila.get(3)) : "");
        dto.setFecha(fila.size() > 4 && fila.get(4) != null ? String.valueOf(fila.get(4)) : "");
        dto.setCategoria(fila.size() > 5 && fila.get(5) != null ? String.valueOf(fila.get(5)) : "");
        dto.setImagen(fila.size() > 6 && fila.get(6) != null ? String.valueOf(fila.get(6)) : "");

        if (fila.size() > 7 && fila.get(7) != null) {
            String destacadoStr = String.valueOf(fila.get(7));
            dto.setDestacado("SI".equalsIgnoreCase(destacadoStr));
        } else {
            dto.setDestacado(false);
        }

        dto.setEstado(fila.size() > 8 && fila.get(8) != null ? String.valueOf(fila.get(8)) : "BORRADOR");
        dto.setAutor(fila.size() > 9 && fila.get(9) != null ? String.valueOf(fila.get(9)) : "");

        return dto;
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam String id,
                                 @RequestParam String nuevoEstado,
                                 HttpSession session) {
        try {
            List<List<Object>> articulosSheets = googleSheetsService.leerArticulosBlog();
            if (articulosSheets == null) {
                return "redirect:/admin/blog?error";
            }
            
            // Buscar el artículo
            List<Object> filaArticulo = articulosSheets.stream()
                    .filter(fila -> fila.size() > 0 && id.equals(String.valueOf(fila.get(0))))
                    .findFirst()
                    .orElse(null);
            
            if (filaArticulo == null) {
                return "redirect:/admin/blog?error";
            }
            
            // Obtener datos actuales
            String titulo = filaArticulo.size() > 1 ? String.valueOf(filaArticulo.get(1)) : "";
            String resumen = filaArticulo.size() > 2 ? String.valueOf(filaArticulo.get(2)) : "";
            String contenido = filaArticulo.size() > 3 ? String.valueOf(filaArticulo.get(3)) : "";
            String fecha = filaArticulo.size() > 4 ? String.valueOf(filaArticulo.get(4)) : "";
            String categoria = filaArticulo.size() > 5 ? String.valueOf(filaArticulo.get(5)) : "";
            String imagen = filaArticulo.size() > 6 ? String.valueOf(filaArticulo.get(6)) : "";
            String destacado = filaArticulo.size() > 7 ? String.valueOf(filaArticulo.get(7)) : "NO";
            String autor = filaArticulo.size() > 9 ? String.valueOf(filaArticulo.get(9)) : "";
            
            // Guardar con nuevo estado
            googleSheetsService.guardarArticuloBlog(id, titulo, resumen, contenido, fecha, 
                    categoria, imagen, destacado, nuevoEstado, autor);
            
            log.info("Estado del artículo {} cambiado a {}", id, nuevoEstado);
            return "redirect:/admin/blog?ok";
        } catch (Exception e) {
            log.error("Error al cambiar estado del artículo", e);
            return "redirect:/admin/blog?error";
        }
    }

    @GetMapping("/listado")
    public String listarArticulos(@RequestParam(defaultValue = "0") int page,
                                   HttpSession session,
                                   Model model) {
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        try {
            List<List<Object>> articulosSheets = googleSheetsService.leerArticulosBlog();
            if (articulosSheets == null) {
                articulosSheets = List.of();
            }
            
            // Convertir a DTOs para facilitar el manejo en la vista
            List<ArticuloBlogDTO> articulos = articulosSheets.stream()
                .skip(1) // Saltar header
                .filter(fila -> fila.size() >= 9) // Asegurar que tenga todas las columnas
                .map(fila -> {
                    ArticuloBlogDTO dto = new ArticuloBlogDTO();
                    
                    // ID como Integer
                    if (fila.get(0) != null) {
                        try {
                            dto.setId(Integer.parseInt(String.valueOf(fila.get(0))));
                        } catch (NumberFormatException e) {
                            dto.setId(null);
                        }
                    }
                    
                    dto.setTitulo(fila.get(1) != null ? String.valueOf(fila.get(1)) : "");
                    dto.setResumen(fila.get(2) != null ? String.valueOf(fila.get(2)) : "");
                    dto.setContenido(fila.get(3) != null ? String.valueOf(fila.get(3)) : "");
                    dto.setFecha(fila.get(4) != null ? String.valueOf(fila.get(4)) : "");
                    dto.setCategoria(fila.get(5) != null ? String.valueOf(fila.get(5)) : "");
                    dto.setImagen(fila.get(6) != null ? String.valueOf(fila.get(6)) : "");
                    
                    // Destacado como Boolean
                    if (fila.get(7) != null) {
                        String destacadoStr = String.valueOf(fila.get(7));
                        dto.setDestacado("SI".equalsIgnoreCase(destacadoStr));
                    } else {
                        dto.setDestacado(false);
                    }
                    
                    dto.setEstado(fila.get(8) != null ? String.valueOf(fila.get(8)) : "BORRADOR");
                    dto.setAutor(fila.size() > 9 && fila.get(9) != null ? String.valueOf(fila.get(9)) : "");
                    return dto;
                })
                .toList();
            
            // Paginación
            int pageSize = 20;
            int totalArticulos = articulos.size();
            int totalPages = (int) Math.ceil((double) totalArticulos / pageSize);
                int start = page * pageSize;
                int end = Math.min(start + pageSize, totalArticulos);

                List<ArticuloBlogDTO> articulosPagina = (start >= totalArticulos || start < 0)
                    ? List.of()
                    : articulos.subList(start, end);
            
            // Estadísticas
            long articulosPublicados = articulos.stream()
                .filter(a -> "PUBLICADO".equalsIgnoreCase(a.getEstado()))
                .count();
            
            long articulosBorradores = articulos.stream()
                .filter(a -> "BORRADOR".equalsIgnoreCase(a.getEstado()))
                .count();
            
            long articulosDestacados = articulos.stream()
                .filter(a -> Boolean.TRUE.equals(a.getDestacado()))
                .count();
            
            model.addAttribute("articulos", articulosPagina);
            model.addAttribute("totalArticulos", totalArticulos);
            model.addAttribute("articulosPublicados", articulosPublicados);
            model.addAttribute("articulosBorradores", articulosBorradores);
            model.addAttribute("articulosDestacados", articulosDestacados);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            
        } catch (Exception e) {
            log.error("Error al obtener artículos", e);
            model.addAttribute("error", "No se pudieron cargar los artículos");
            model.addAttribute("articulos", List.of());
            model.addAttribute("totalArticulos", 0);
            model.addAttribute("articulosPublicados", 0);
            model.addAttribute("articulosBorradores", 0);
            model.addAttribute("articulosDestacados", 0);
        }
        
        return "admin/blog/listado";
    }

    @GetMapping("/nuevo")
    public String nuevoArticulo(HttpSession session, Model model) {
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        try {
            // Generar ID automáticamente
            List<List<Object>> articulosSheets = googleSheetsService.leerArticulosBlog();
            if (articulosSheets == null) {
                articulosSheets = List.of();
            }
            int maxId = 0;
            
            for (int i = 1; i < articulosSheets.size(); i++) {
                List<Object> fila = articulosSheets.get(i);
                if (fila.size() > 0 && fila.get(0) != null) {
                    try {
                        int currentId = Integer.parseInt(String.valueOf(fila.get(0)));
                        if (currentId > maxId) maxId = currentId;
                    } catch (NumberFormatException ignored) {}
                }
            }
            
            ArticuloBlogDTO nuevoArticulo = new ArticuloBlogDTO();
            nuevoArticulo.setId(maxId + 1);
            nuevoArticulo.setAutor(String.valueOf(session.getAttribute("nombreUsuario")));
            nuevoArticulo.setEstado("BORRADOR");
            nuevoArticulo.setDestacado(false);
            
            model.addAttribute("articulo", nuevoArticulo);
        } catch (Exception e) {
            log.error("Error al generar ID de artículo", e);
            model.addAttribute("articulo", new ArticuloBlogDTO());
        }
        
        return "admin/blog/form";
    }

    @GetMapping("/editar")
    public String editarArticulo(@RequestParam String id,
                                  HttpSession session,
                                  Model model) {
        model.addAttribute("nombreUsuario", session.getAttribute("nombreUsuario"));
        
        try {
            List<List<Object>> articulosSheets = googleSheetsService.leerArticulosBlog();
            List<Object> filaArticulo = articulosSheets.stream()
                    .filter(fila -> fila.size() > 0 && id.equals(String.valueOf(fila.get(0))))
                    .findFirst()
                    .orElse(null);
            
            if (filaArticulo == null) {
                return "redirect:/admin/blog?error";
            }
            
            // Convertir a DTO
            ArticuloBlogDTO articulo = new ArticuloBlogDTO();
            
            // ID como Integer
            if (filaArticulo.get(0) != null) {
                try {
                    articulo.setId(Integer.parseInt(String.valueOf(filaArticulo.get(0))));
                } catch (NumberFormatException e) {
                    articulo.setId(null);
                }
            }
            
            articulo.setTitulo(filaArticulo.size() > 1 && filaArticulo.get(1) != null ? String.valueOf(filaArticulo.get(1)) : "");
            articulo.setResumen(filaArticulo.size() > 2 && filaArticulo.get(2) != null ? String.valueOf(filaArticulo.get(2)) : "");
            articulo.setContenido(filaArticulo.size() > 3 && filaArticulo.get(3) != null ? String.valueOf(filaArticulo.get(3)) : "");
            articulo.setFecha(filaArticulo.size() > 4 && filaArticulo.get(4) != null ? String.valueOf(filaArticulo.get(4)) : "");
            articulo.setCategoria(filaArticulo.size() > 5 && filaArticulo.get(5) != null ? String.valueOf(filaArticulo.get(5)) : "");
            articulo.setImagen(filaArticulo.size() > 6 && filaArticulo.get(6) != null ? String.valueOf(filaArticulo.get(6)) : "");
            
            // Destacado como Boolean
            if (filaArticulo.size() > 7 && filaArticulo.get(7) != null) {
                String destacadoStr = String.valueOf(filaArticulo.get(7));
                articulo.setDestacado("SI".equalsIgnoreCase(destacadoStr));
            } else {
                articulo.setDestacado(false);
            }
            
            articulo.setEstado(filaArticulo.size() > 8 && filaArticulo.get(8) != null ? String.valueOf(filaArticulo.get(8)) : "BORRADOR");
            articulo.setAutor(filaArticulo.size() > 9 && filaArticulo.get(9) != null ? String.valueOf(filaArticulo.get(9)) : "");
            
            model.addAttribute("articulo", articulo);
            
            return "admin/blog/form";
            
        } catch (Exception e) {
            log.error("Error al obtener artículo", e);
            return "redirect:/admin/blog?error";
        }
    }

    @PostMapping("/guardar")
    public String guardarArticulo(@RequestParam(required = false) String id,
                                   @RequestParam String titulo,
                                   @RequestParam String resumen,
                                   @RequestParam String contenido,
                                   @RequestParam String fecha,
                                   @RequestParam String categoria,
                                   @RequestParam(required = false) String imagen,
                                   @RequestParam(defaultValue = "NO") String destacado,
                                   @RequestParam(defaultValue = "BORRADOR") String estado,
                                   @RequestParam(required = false) String autor,
                                   HttpSession session) {
        try {
            // Validaciones básicas
            if (titulo == null || titulo.isBlank()) {
                return "redirect:/admin/blog/listado?error=titulo";
            }
            
            // Si no hay ID, generar uno
            String idFinal = id;
            if (idFinal == null || idFinal.isBlank()) {
                List<List<Object>> articulosSheets = googleSheetsService.leerArticulosBlog();
                int maxId = 0;
                for (int i = 1; i < articulosSheets.size(); i++) {
                    List<Object> fila = articulosSheets.get(i);
                    if (fila.size() > 0 && fila.get(0) != null) {
                        try {
                            int currentId = Integer.parseInt(String.valueOf(fila.get(0)));
                            if (currentId > maxId) maxId = currentId;
                        } catch (NumberFormatException ignored) {}
                    }
                }
                idFinal = String.valueOf(maxId + 1);
            }
            
            String autorFinal = autor != null && !autor.isBlank() ? autor : String.valueOf(session.getAttribute("nombreUsuario"));
            String imagenFinal = imagen != null && !imagen.isBlank() ? imagen : "";
            
            googleSheetsService.guardarArticuloBlog(idFinal, titulo, resumen, contenido, fecha, categoria, imagenFinal, destacado, estado, autorFinal);
            return "redirect:/admin/blog?ok";
        } catch (Exception e) {
            log.error("Error al guardar artículo", e);
            return "redirect:/admin/blog?error";
        }
    }
    
    /**
     * ENDPOINT TEMPORAL: Migra las 5 noticias antiguas a Google Sheets
     * Ejecutar una sola vez: http://localhost:8080/admin/blog/migrar-antiguas
     */
    @GetMapping("/migrar-antiguas")
    public String migrarNoticiasAntiguas(HttpSession session) {
        try {
            log.info("Iniciando migración de noticias antiguas...");
            
            // Artículo 1 - Destacado
            googleSheetsService.guardarArticuloBlog(
                "1",
                "MTC oficializa prohibición de dos personas en moto en zonas de emergencia",
                "Nueva normativa establece multas de hasta S/ 660 y retención del vehículo para quienes circulen con acompañante en distritos declarados en emergencia.",
                "<p>El Gobierno ha ratificado, mediante el <strong>Decreto Supremo N° 002-2026-MTC</strong>, la restricción del traslado de acompañantes en motocicletas lineales en zonas bajo estado de emergencia como Lima y Callao.</p><br><h4>Detalles Técnicos de la Norma:</h4><ul><li><strong>Infracción G58:</strong> Circular con acompañante en zonas restringidas conlleva una multa de S/ 660 (12% UIT).</li><li><strong>Puntos:</strong> Se restarán 50 puntos del historial del conductor por cada infracción cometida.</li><li><strong>Retención:</strong> La Policía Nacional está facultada para internar el vehículo en el depósito municipal de inmediato.</li><li><strong>Excepciones:</strong> Solo personal de seguridad y emergencias debidamente identificados están exentos.</li></ul><br><p>En <strong>Autovías Seguras</strong> te recomendamos portar siempre tus documentos originales. La fiscalización será rigurosa en puntos estratégicos de la ciudad.</p>",
                "20/01/2026",
                "Actualidad",
                "https://res.cloudinary.com/dtozni6ik/image/upload/v1769718980/000149725M_x7y9oy.jpg",
                "SI",
                "PUBLICADO",
                "Admin"
            );
            
            // Artículo 2
            googleSheetsService.guardarArticuloBlog(
                "2",
                "Nuevo régimen temporal para licencias profesionales",
                "El MTC busca facilitar la recategorización de conductores mediante programas de entrenamiento específicos.",
                "<p>El <strong>Decreto Supremo N° 003-2026-MTC</strong> establece un marco legal de dos años para cerrar la brecha de conductores profesionales de carga y pasajeros.</p><br><h4>¿Qué beneficios ofrece?</h4><p>Se ha simplificado el acceso a las categorías AII y AIII para conductores con récord impecable. Las horas de instrucción teórica se han modernizado incluyendo módulos de primeros auxilios y psicología vial.</p><br><h4>Pasos para acogerte:</h4><ul><li>Verificar que no posees multas \"Muy Graves\" sin pagar.</li><li>Aprobar el examen psicosomático en <strong>Autovías Seguras</strong>.</li><li>Inscribirte en el Programa de Formación de Conductores autorizado por el MTC.</li></ul>",
                "16/01/2026",
                "Actualidad",
                "https://res.cloudinary.com/dtozni6ik/image/upload/v1769719683/1334139-whatsapp-image-2026-01-15-at-7-58-01-pm_dpzims.jpg",
                "NO",
                "PUBLICADO",
                "Admin"
            );
            
            // Artículo 3
            googleSheetsService.guardarArticuloBlog(
                "3",
                "Brevete Electrónico 2026: Todo lo que debes saber",
                "Conoce las ventajas de la licencia digital y cómo evitar estafas en el trámite online.",
                "<p>La licencia electrónica tiene la misma validez que la física, pero con mayor seguridad y menor costo (S/ 6.70).</p><br><h4>Seguridad y Fiscalización:</h4><ul><li><strong>Código QR:</strong> Los inspectores de la ATU y PNP escanean el código para ver tu historial en tiempo real.</li><li><strong>Descarga:</strong> Puedes llevarla en la app \"MTC Digital\" sin necesidad de conexión constante a datos.</li><li><strong>Duplicados:</strong> En caso de robo de celular, puedes descargarla nuevamente sin pagar tasas adicionales.</li></ul><br><p>Recuerda: El examen médico es el único paso que <strong>obligatoriamente</strong> es presencial. Visítanos para subir tus resultados al sistema nacional.</p>",
                "12/01/2026",
                "Actualidad",
                "https://res.cloudinary.com/dtozni6ik/image/upload/v1769720251/Licencia-de-conducir-virtual-requisitos_ztudq4.jpg",
                "NO",
                "PUBLICADO",
                "Admin"
            );
            
            // Artículo 4
            googleSheetsService.guardarArticuloBlog(
                "4",
                "Impacto de la salud visual en la seguridad vial",
                "¿Sabías que el 80% de la información al conducir entra por los ojos? La importancia de un chequeo real.",
                "<p>Muchos conductores ignoran problemas de visión nocturna o fatiga visual, lo que incrementa el riesgo de accidentes en un 30% durante la noche.</p><br><h4>Qué evaluamos en Autovías Seguras:</h4><ul><li><strong>Agudeza Visual:</strong> Capacidad para identificar señales a distancia.</li><li><strong>Test de Ishihara:</strong> Detección de daltonismo para interpretar semáforos correctamente.</li><li><strong>Estereopsis:</strong> Percepción de profundidad (distancia real entre vehículos).</li></ul><br><p>Un examen médico honesto no es solo un trámite, es tu seguro de vida al volante. Si usas lentes, recuerda traer tu medida actualizada el día de tu cita.</p>",
                "08/01/2026",
                "Salud",
                "https://res.cloudinary.com/dtozni6ik/image/upload/v1769720248/invierno_aoxkh6.jpg",
                "NO",
                "PUBLICADO",
                "Admin"
            );
            
            // Artículo 5
            googleSheetsService.guardarArticuloBlog(
                "5",
                "5 Errores críticos en el examen de manejo",
                "Evita las faltas que descalifican automáticamente a los postulantes en el circuito de evaluación.",
                "<p>Aprobar el examen práctico requiere técnica y, sobre todo, control de nervios. Aquí te detallamos las faltas eliminatorias:</p><br><ol><li><strong>No usar el cinturón:</strong> Es una falta muy grave que termina el examen antes de empezar.</li><li><strong>Pisar las líneas amarillas:</strong> Indica falta de control sobre las dimensiones del vehículo.</li><li><strong>No usar direccionales:</strong> Debes señalizar con 20 metros de anticipación en cada giro.</li><li><strong>Ignorar la señal de Pare:</strong> Debes detenerte totalmente por al menos 3 segundos.</li><li><strong>No respetar al peatón:</strong> Prioridad absoluta en los cruces señalizados.</li></ol><br><p>En nuestra <strong>Escuela de Conductores</strong>, realizamos simulacros idénticos al examen real para que vayas con total confianza.</p>",
                "05/01/2026",
                "MTC",
                "https://res.cloudinary.com/dtozni6ik/image/upload/v1769720246/MTC_ry3mv7.jpg",
                "NO",
                "PUBLICADO",
                "Admin"
            );
            
            log.info("Migración completada: 5 artículos guardados");
            return "redirect:/admin/blog?ok";
            
        } catch (Exception e) {
            log.error("Error en migración de noticias antiguas", e);
            return "redirect:/admin/blog?error";
        }
    }
}
