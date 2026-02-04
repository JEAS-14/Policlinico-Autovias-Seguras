package com.policlinico.autovias.application.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GoogleSheetsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsService.class);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${app.sheets.spreadsheet-id}")
    private String spreadsheetId;

    @Value("${app.sheets.credentials-path:}")
    private String credentialsPath;
    
    @Value("${app.sheets.consultas-sheet}")
    private String consultasSheet;
    
    @Value("${app.sheets.reclamaciones-sheet}")
    private String reclamacionesSheet;
    
    @Value("${app.sheets.blog-sheet}")
    private String blogSheet;

    private final ResourceLoader resourceLoader;

    public GoogleSheetsService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Guarda una consulta en Google Sheets (pestaña "Consultas")
     */
    public void guardarConsulta(String nombre, String apellido, String email, String telefono,
                                 String tipoConsulta, String mensaje, String numeroTicket) {
        try {
            LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Lima"));
            List<Object> fila = new ArrayList<>(Arrays.asList(
                    ahora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    nombre,
                    apellido,
                    email,
                    telefono,
                    tipoConsulta,
                    mensaje,
                    "PENDIENTE",
                    numeroTicket
            ));

            agregarFilaASheets(consultasSheet, fila);
            logger.info("Consulta guardada en Google Sheets: Ticket {}", numeroTicket);
        } catch (Exception e) {
            logger.error("Error al guardar consulta en Google Sheets", e);
        }
    }

    /**
     * Guarda una reclamación en Google Sheets (pestaña "Reclamaciones")
     */
    public void guardarReclamacion(String nombre, String apellido, String dni, String email,
                                    String telefono, String direccion, String tipo, String detalles,
                                    String solicitud, String numeroTicket) {
        try {
            logger.info("Intentando guardar reclamación en Google Sheets: {}", numeroTicket);
            logger.info("Tipo recibido: {}", tipo);
            
            LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Lima"));
            List<Object> fila = new ArrayList<>(Arrays.asList(
                    ahora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    nombre,
                    apellido,
                    dni,
                    email,
                    telefono,
                    direccion,
                    tipo,
                    detalles,
                    solicitud != null ? solicitud : "",
                    numeroTicket
            ));

            logger.info("Fila a insertar: {} elementos", fila.size());
            agregarFilaASheets(reclamacionesSheet, fila);
            logger.info("Reclamación guardada exitosamente en Google Sheets: Ticket {} - Tipo: {}", numeroTicket, tipo);
        } catch (Exception e) {
            logger.error("Error al guardar reclamación en Google Sheets. Ticket: {}, Error: {}", numeroTicket, e.getMessage(), e);
            throw new RuntimeException("Error al guardar en Google Sheets", e);
        }
    }

    /**
     * Agrega una fila al final de una hoja específica
     */
    private void agregarFilaASheets(String nombreHoja, List<Object> fila) throws IOException {
        try {
            logger.info("Conectando a Google Sheets para hoja: {}", nombreHoja);
            Sheets service = getSheetService();

            // Encontrar la primera fila vacía
            String range = nombreHoja + "!A:A";
            logger.info("Buscando última fila en rango: {}", range);
            
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            int filaInsertarEn = (values != null ? values.size() : 0) + 1;
            
            logger.info("Insertando en fila: {}", filaInsertarEn);

            // Insertar la nueva fila
            String insertRange = nombreHoja + "!A" + filaInsertarEn;
            logger.info("Rango de inserción: {}", insertRange);
            
            ValueRange body = new ValueRange()
                    .setValues(Arrays.asList(fila));

            service.spreadsheets().values()
                    .update(spreadsheetId, insertRange, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
                    
            logger.info("Fila insertada exitosamente en {}", insertRange);
        } catch (Exception e) {
            logger.error("Error detallado al agregar fila a hoja '{}': {}", nombreHoja, e.getMessage(), e);
            throw new IOException("Error al agregar fila a Google Sheets en hoja: " + nombreHoja, e);
        }
    }

    /**
     * Obtiene la instancia del servicio de Sheets autenticado
     */
    private Sheets getSheetService() throws IOException {
        try {
            InputStream inputStream;
            
            // Primero intenta leer desde variable de entorno
            String credentialsJson = System.getenv("GOOGLE_CREDENTIALS_JSON");
            
            if (credentialsJson != null && !credentialsJson.isEmpty()) {
                // Usar credenciales desde variable de entorno
                inputStream = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
                logger.info("Usando credenciales de Google Sheets desde variable de entorno");
            } else if (credentialsPath != null && !credentialsPath.isEmpty()) {
                // Usar credenciales desde archivo (fallback para desarrollo local)
                Resource resource = resourceLoader.getResource(credentialsPath);
                inputStream = resource.getInputStream();
                logger.info("Usando credenciales de Google Sheets desde archivo: {}", credentialsPath);
            } else {
                throw new IOException("No se encontraron credenciales de Google Sheets. Configure GOOGLE_CREDENTIALS_JSON o app.sheets.credentials-path");
            }

            ServiceAccountCredentials credentials = ServiceAccountCredentials
                    .fromStream(inputStream);

            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName("Policlinico-Autovias-Seguras").build();
        } catch (Exception e) {
            throw new IOException("Error al crear el servicio de Google Sheets", e);
        }
    }

    /**
     * Lee todos los artículos del blog desde Google Sheets
     */
    public List<List<Object>> leerArticulosBlog() {
        try {
            logger.info("Leyendo artículos del blog desde Google Sheets: hoja {}", blogSheet);
            Sheets service = getSheetService();
            
            String range = blogSheet + "!A:J"; // Columnas A-J (ID hasta Autor)
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                logger.warn("No se encontraron artículos en la hoja {}", blogSheet);
                return new ArrayList<>();
            }
            
            logger.info("Se encontraron {} filas en la hoja de blog", values.size());
            return values;
            
        } catch (Exception e) {
            logger.error("Error al leer artículos del blog desde Google Sheets", e);
            throw new RuntimeException("Error al leer artículos del blog", e);
        }
    }
    
    /**
     * Lee todas las reclamaciones desde Google Sheets
     */
    public List<List<Object>> leerReclamaciones() {
        try {
            logger.info("Leyendo reclamaciones desde Google Sheets: hoja {}", reclamacionesSheet);
            Sheets service = getSheetService();
            
            String range = reclamacionesSheet + "!A:O"; // Columnas A-O (Fecha hasta Estado/Respuesta)
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                logger.warn("No se encontraron reclamaciones en la hoja {}", reclamacionesSheet);
                return new ArrayList<>();
            }
            
            logger.info("Se encontraron {} reclamaciones en Google Sheets", values.size() - 1); // -1 por el header
            return values;
            
        } catch (Exception e) {
            logger.error("Error al leer reclamaciones desde Google Sheets", e);
            throw new RuntimeException("Error al leer reclamaciones: " + e.getMessage(), e);
        }
    }

    public List<Object> buscarReclamacionPorTicket(String ticket) {
        List<List<Object>> values = leerReclamaciones();
        if (values == null || values.size() <= 1) {
            return null;
        }

        for (int i = 1; i < values.size(); i++) { // saltar header
            List<Object> fila = values.get(i);
            if (fila.size() > 10) {
                String t = String.valueOf(fila.get(10));
                if (t != null && t.trim().equalsIgnoreCase(ticket != null ? ticket.trim() : "")) {
                return fila;
                }
            }
        }

        return null;
    }

    public void actualizarReclamacion(String ticket, String estado, String respuesta, String respondidoPor) {
        try {
            Sheets service = getSheetService();
            List<List<Object>> values = leerReclamaciones();

            int rowIndex = -1;
            for (int i = 1; i < values.size(); i++) { // saltar header
                List<Object> fila = values.get(i);
                if (fila.size() > 10 && ticket.equals(String.valueOf(fila.get(10)))) {
                    rowIndex = i + 1; // 1-based para Sheets
                    break;
                }
            }

            if (rowIndex == -1) {
                throw new RuntimeException("No se encontró el ticket: " + ticket);
            }

            LocalDateTime ahoraRespuesta = LocalDateTime.now(ZoneId.of("America/Lima"));
            String fechaRespuesta = ahoraRespuesta.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            List<Object> updateValues = Arrays.asList(
                    estado,
                    respuesta != null ? respuesta : "",
                    respondidoPor != null ? respondidoPor : "",
                    fechaRespuesta
            );

            String updateRange = reclamacionesSheet + "!L" + rowIndex + ":O" + rowIndex;
            ValueRange body = new ValueRange().setValues(Arrays.asList(updateValues));

            service.spreadsheets().values()
                    .update(spreadsheetId, updateRange, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

            logger.info("Reclamación {} actualizada: estado={}, respondidoPor={}", ticket, estado, respondidoPor);

        } catch (Exception e) {
            logger.error("Error al actualizar reclamación en Google Sheets", e);
            throw new RuntimeException("Error al actualizar reclamación: " + e.getMessage(), e);
        }
    }

    public void guardarArticuloBlog(String id, String titulo, String resumen, String contenido, String fecha, 
                                     String categoria, String imagen, String destacado, String estado, String autor) {
        try {
            Sheets service = getSheetService();
            List<List<Object>> values = leerArticulosBlog();

            // Buscar si existe el artículo con ese ID
            int rowIndex = -1;
            for (int i = 1; i < values.size(); i++) {
                List<Object> fila = values.get(i);
                if (fila.size() > 0 && id.equals(String.valueOf(fila.get(0)))) {
                    rowIndex = i + 1; // 1-based
                    break;
                }
            }

            List<Object> articuloData = Arrays.asList(
                    id, titulo, resumen, contenido, fecha, categoria, imagen, destacado, estado, autor
            );

            if (rowIndex == -1) {
                // Nuevo artículo - agregar al final
                int nextRow = values.size() + 1;
                String insertRange = blogSheet + "!A" + nextRow;
                ValueRange body = new ValueRange().setValues(Arrays.asList(articuloData));
                
                service.spreadsheets().values()
                        .update(spreadsheetId, insertRange, body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
                
                logger.info("Artículo creado con ID: {}", id);
            } else {
                // Actualizar artículo existente
                String updateRange = blogSheet + "!A" + rowIndex + ":J" + rowIndex;
                ValueRange body = new ValueRange().setValues(Arrays.asList(articuloData));
                
                service.spreadsheets().values()
                        .update(spreadsheetId, updateRange, body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
                
                logger.info("Artículo actualizado con ID: {}", id);
            }
            
        } catch (Exception e) {
            logger.error("Error al guardar artículo en Google Sheets", e);
            throw new RuntimeException("Error al guardar artículo: " + e.getMessage(), e);
        }
    }
}
