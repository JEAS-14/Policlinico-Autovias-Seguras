package com.policlinico.autovias.infrastructure.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.policlinico.autovias.domain.exception.RateLimitExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String handleRateLimitExceeded(RateLimitExceededException ex, Model model) {
        model.addAttribute("errorTitle", "Demasiadas solicitudes");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorDetails",
                "Has alcanzado el límite de " + 5 + " solicitudes por día. "
                        + "Por favor, intenta nuevamente mañana.");
        return "error/rate-limit-error";
    }
}
