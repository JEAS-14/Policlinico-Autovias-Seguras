package com.policlinico.autovias.service;

import com.policlinico.autovias.model.dto.ConsultaDTO;
import com.policlinico.autovias.model.entity.Consulta;
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
}