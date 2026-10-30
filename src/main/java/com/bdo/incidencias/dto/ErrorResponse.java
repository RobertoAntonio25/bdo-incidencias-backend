package com.bdo.incidencias.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensaje;
    private List<String> detalles;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String error, String mensaje) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
    }

    public ErrorResponse(int status, String error, String mensaje, List<String> detalles) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.detalles = detalles;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<String> getDetalles() {
        return detalles;
    }

}
