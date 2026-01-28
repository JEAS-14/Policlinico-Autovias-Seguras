package com.policlinico.autovias.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el formulario de consulta/cotización
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;
    
    @NotBlank(message = "Seleccione un tipo de consulta")
    private String tipoConsulta;
    
    private String empresa; // Opcional
    
    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;
}