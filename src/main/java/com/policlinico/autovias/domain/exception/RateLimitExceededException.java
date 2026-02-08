package com.policlinico.autovias.domain.exception;

public class RateLimitExceededException extends RuntimeException {

    private final int remainingRequests;
    private final int windowHours;

    public RateLimitExceededException(int remainingRequests, int windowHours) {
        super(String.format(
                "Demasiadas solicitudes. Por favor, intenta de nuevo en %d hora(s).",
                windowHours));
        this.remainingRequests = remainingRequests;
        this.windowHours = windowHours;
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }

    public int getWindowHours() {
        return windowHours;
    }
}
