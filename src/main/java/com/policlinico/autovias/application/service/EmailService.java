package com.policlinico.autovias.application.service;

import com.policlinico.autovias.application.dto.ConsultaDTO;
import com.policlinico.autovias.application.dto.ReclamacionDTO;
import com.policlinico.autovias.domain.entity.Consulta;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.destinatario}")
    private String emailDestinatario;

    @Value("${spring.mail.username}")
    private String emailRemitente;

    /**
     * Envía email de notificación al equipo del policlínico
     */
    public void enviarNotificacionConsulta(ConsultaDTO consulta, String numeroTicket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(emailDestinatario);
            helper.setCc(emailRemitente); // Copia al remitente para que llegue a "Recibidos"
            helper.setSubject("🔔 Nueva Consulta - Ticket " + numeroTicket);

            Context context = new Context();
            context.setVariable("numeroTicket", numeroTicket);
            context.setVariable("nombre", consulta.getNombre() + " " + consulta.getApellido());
            context.setVariable("email", consulta.getEmail());
            context.setVariable("telefono", consulta.getTelefono());
            context.setVariable("tipoConsulta", consulta.getTipoConsulta());
            context.setVariable("empresa", consulta.getEmpresa() != null ? consulta.getEmpresa() : "N/A");
            context.setVariable("mensaje", consulta.getMensaje());
            context.setVariable("fecha", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            ));

            String htmlContent = templateEngine.process("email/notificacion-consulta", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de notificación enviado a {} para ticket {}", emailDestinatario, numeroTicket);

        } catch (MessagingException e) {
            log.error("Error al enviar email de notificación para ticket {}", numeroTicket, e);
            throw new RuntimeException("Error al enviar notificación por email", e);
        }
    }

    /**
     * Envía email de confirmación al cliente
     */
    public void enviarConfirmacionCliente(ConsultaDTO consulta, String numeroTicket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(consulta.getEmail());
            helper.setSubject("✅ Hemos recibido tu consulta - Ticket " + numeroTicket);

            Context context = new Context();
            context.setVariable("numeroTicket", numeroTicket);
            context.setVariable("nombre", consulta.getNombre());
            context.setVariable("tipoConsulta", consulta.getTipoConsulta());

            String htmlContent = templateEngine.process("email/confirmacion-cliente", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de confirmación enviado a {} para ticket {}", consulta.getEmail(), numeroTicket);

        } catch (MessagingException e) {
            log.error("Error al enviar email de confirmación al cliente para ticket {}", numeroTicket, e);
        }
    }

    /**
     * Envía la respuesta del policlínico al cliente
     */
    public void enviarRespuestaCliente(Consulta consulta) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(consulta.getEmail());
            helper.setReplyTo(emailDestinatario); // Para que el cliente pueda responder directamente
            helper.setSubject("📩 Respuesta a tu consulta - Ticket " + consulta.getNumeroTicket());

            Context context = new Context();
            context.setVariable("numeroTicket", consulta.getNumeroTicket());
            context.setVariable("nombre", consulta.getNombre());
            context.setVariable("respuesta", consulta.getRespuesta());
            context.setVariable("respondidoPor", consulta.getRespondidoPor());
            context.setVariable("fechaRespuesta", consulta.getFechaRespuesta().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            ));
            
            // Si hay cita programada
            if (consulta.getFechaCitaProgramada() != null) {
                context.setVariable("tieneCita", true);
                context.setVariable("fechaCita", consulta.getFechaCitaProgramada().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                ));
            } else {
                context.setVariable("tieneCita", false);
            }

            String htmlContent = templateEngine.process("email/respuesta-cliente", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Respuesta enviada al cliente {} para ticket {}", 
                consulta.getEmail(), consulta.getNumeroTicket());

        } catch (MessagingException e) {
            log.error("Error al enviar respuesta al cliente para ticket {}", 
                consulta.getNumeroTicket(), e);
            throw new RuntimeException("Error al enviar respuesta por email", e);
        }
    }

    /**
     * Envía email de notificación de reclamación
     */
    public void enviarNotificacionReclamacion(ReclamacionDTO reclamacion) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(emailDestinatario);
            helper.setCc(emailRemitente); // Copia al remitente para que llegue a "Recibidos"
            helper.setSubject("📋 Nueva Reclamación - Libro de Reclamaciones");

            Context context = new Context();
            context.setVariable("nombre", reclamacion.getNombre());
            context.setVariable("apellido", reclamacion.getApellido());
            context.setVariable("dniCe", reclamacion.getDniCe());
            context.setVariable("domicilio", reclamacion.getDomicilio());
            context.setVariable("telefono", reclamacion.getTelefono());
            context.setVariable("email", reclamacion.getEmail());
            context.setVariable("tipo", reclamacion.getTipo());
            context.setVariable("detalle", reclamacion.getDetalle());
            context.setVariable("pedido", reclamacion.getPedido() != null ? reclamacion.getPedido() : "N/A");
            context.setVariable("fecha", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            ));

            String htmlContent = templateEngine.process("email/notificacion-reclamacion", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de reclamación enviado a {} para {}", emailDestinatario, reclamacion.getNombre() + " " + reclamacion.getApellido());

        } catch (MessagingException e) {
            log.error("Error al enviar email de reclamación para {}", reclamacion.getNombre() + " " + reclamacion.getApellido(), e);
            throw new RuntimeException("Error al enviar notificación de reclamación por email", e);
        }
    }

    /**
     * Envía email de confirmación al reclamante
     */
    public void enviarConfirmacionReclamante(ReclamacionDTO reclamacion) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(reclamacion.getEmail());
            helper.setSubject("✅ Reclamación Enviada - Policlínico Autovías Seguras");

            Context context = new Context();
            context.setVariable("nombre", reclamacion.getNombre());
            context.setVariable("apellido", reclamacion.getApellido());
            context.setVariable("tipo", reclamacion.getTipo());
            context.setVariable("fecha", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            ));

            String htmlContent = templateEngine.process("email/confirmacion-reclamacion", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Confirmación enviada al reclamante {} {}", reclamacion.getNombre(), reclamacion.getApellido());

        } catch (MessagingException e) {
            log.error("Error al enviar confirmación al reclamante {}", reclamacion.getEmail(), e);
            throw new RuntimeException("Error al enviar confirmación por email", e);
        }
    }

    public void enviarRespuestaReclamacion(String email, String nombre, String apellido, String ticket, String estado, String respuesta) {
        try {
            if (email == null || email.isBlank()) {
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailRemitente);
            helper.setTo(email);
            helper.setReplyTo(emailDestinatario);
            helper.setSubject("📩 Respuesta a tu reclamación - Ticket " + ticket);

            Context context = new Context();
            context.setVariable("numeroTicket", ticket);
            context.setVariable("nombre", nombre);
            context.setVariable("apellido", apellido);
            context.setVariable("estado", estado);
            context.setVariable("respuesta", respuesta != null ? respuesta : "");
            context.setVariable("fechaRespuesta", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            ));

            String htmlContent = templateEngine.process("email/respuesta-reclamacion", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Respuesta de reclamación enviada a {} para ticket {}", email, ticket);

        } catch (MessagingException e) {
            log.error("Error al enviar respuesta de reclamación para ticket {}", ticket, e);
            throw new RuntimeException("Error al enviar respuesta por email", e);
        }
    }
}