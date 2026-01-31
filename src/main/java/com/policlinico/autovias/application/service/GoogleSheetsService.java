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

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
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

    @Value("${app.sheets.credentials-path}")
    private String credentialsPath;

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
            List<Object> fila = new ArrayList<>(Arrays.asList(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    nombre,
                    apellido,
                    email,
                    telefono,
                    tipoConsulta,
                    mensaje,
                    "PENDIENTE",
                    numeroTicket
            ));

            agregarFilaASheets("Consultas", fila);
            logger.info("Consulta guardada en Google Sheets: Ticket {}", numeroTicket);
        } catch (Exception e) {
            logger.error("Error al guardar consulta en Google Sheets", e);
        }
    }

    /**
     * Guarda una reclamación en Google Sheets (pestaña "Reclamaciones")
     */
    public void guardarReclamacion(String nombre, String apellido, String dni, String email,
                                    String telefono, String direccion, String detalles,
                                    String solicitud, String numeroTicket) {
        try {
            List<Object> fila = new ArrayList<>(Arrays.asList(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    nombre,
                    apellido,
                    dni,
                    email,
                    telefono,
                    direccion,
                    detalles,
                    solicitud,
                    numeroTicket
            ));

            agregarFilaASheets("Reclamaciones", fila);
            logger.info("Reclamación guardada en Google Sheets: Ticket {}", numeroTicket);
        } catch (Exception e) {
            logger.error("Error al guardar reclamación en Google Sheets", e);
        }
    }

    /**
     * Agrega una fila al final de una hoja específica
     */
    private void agregarFilaASheets(String nombreHoja, List<Object> fila) throws IOException {
        Sheets service = getSheetService();

        // Encontrar la primera fila vacía
        String range = nombreHoja + "!A:A";
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();
        int filaInsertarEn = (values != null ? values.size() : 0) + 1;

        // Insertar la nueva fila
        String insertRange = nombreHoja + "!A" + filaInsertarEn;
        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(fila));

        service.spreadsheets().values()
                .update(spreadsheetId, insertRange, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    /**
     * Obtiene la instancia del servicio de Sheets autenticado
     */
    private Sheets getSheetService() throws IOException {
        try {
            Resource resource = resourceLoader.getResource(credentialsPath);
            InputStream inputStream = resource.getInputStream();

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
}
