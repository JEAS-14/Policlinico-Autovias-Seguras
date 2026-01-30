package com.policlinico.autovias.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el formulario de libro de reclamaciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReclamacionDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El DNI/CE es obligatorio")
    @Pattern(regexp = "[0-9]{8,12}", message = "El DNI/CE debe tener 8-12 dígitos numéricos")
    private String dniCe;

    @NotBlank(message = "El domicilio es obligatorio")
    private String domicilio;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "El detalle es obligatorio")
    private String detalle;

    private String pedido; // Opcional
}