package com.bdo.incidencias.dto;

import com.bdo.incidencias.model.EstadoIncidencia;
import jakarta.validation.constraints.NotNull;

public class CambiarEstadoDto {

    @NotNull(message = "El nuevo estado es obligatorio (ABIERTA, EN_CURSO, CERRADA")
    private EstadoIncidencia estado;

    public CambiarEstadoDto() {
    }

    public CambiarEstadoDto(EstadoIncidencia estado) {
        this.estado = estado;
    }

    public EstadoIncidencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoIncidencia estado) {
        this.estado = estado;
    }

}
