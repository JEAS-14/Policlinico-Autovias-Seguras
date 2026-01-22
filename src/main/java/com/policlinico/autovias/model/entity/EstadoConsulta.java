package com.policlinico.autovias.model.entity;

public enum EstadoConsulta {
    PENDIENTE("Pendiente", "badge-warning"),
    EN_PROCESO("En Proceso", "badge-info"),
    RESPONDIDA("Respondida", "badge-success"),
    CITA_PROGRAMADA("Cita Programada", "badge-primary"),
    CERRADA("Cerrada", "badge-secondary");
    
    private final String descripcion;
    private final String cssClass;
    
    EstadoConsulta(String descripcion, String cssClass) {
        this.descripcion = descripcion;
        this.cssClass = cssClass;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public String getCssClass() {
        return cssClass;
    }
}