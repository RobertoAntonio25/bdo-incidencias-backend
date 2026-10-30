package com.bdo.incidencias.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bdo.incidencias.dto.CambiarEstadoDto;
import com.bdo.incidencias.dto.CrearIncidenciaDto;
import com.bdo.incidencias.model.EstadoIncidencia;
import com.bdo.incidencias.model.Incidencia;
import com.bdo.incidencias.service.IncidenciaService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    // Inyección de dependencias por constructor
    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    // POST /api/incidencias -> Crea incidencia (estado ABIERTA), retorna 201 o 400
    @PostMapping
    public ResponseEntity<Incidencia> crearIncidencia(@Valid @RequestBody CrearIncidenciaDto dto) {
        Incidencia nuevIncidencia = incidenciaService.crearIncidencia(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevIncidencia.getId())
                .toUri();

        return ResponseEntity.created(location).body(nuevIncidencia);
    }

    // GET /api/incidencias -> Lista incidencias, con filtro opcional por estado
    @GetMapping
    public ResponseEntity<List<Incidencia>> listarIncidencias(
            @RequestParam(required = false) EstadoIncidencia estado) {
        List<Incidencia> incidencias = incidenciaService.listarIncidencias(estado);
        return ResponseEntity.ok(incidencias);
    }

    // PATCH /api/incidencias/{id}/estado -> Cambia estado. Valida regla CERRADA
    // (409) o no existe (404)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Incidencia> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoDto dto) {
        Incidencia incidenciaActualizada = incidenciaService.cambiarEstado(id, dto.getEstado());
        return ResponseEntity.ok(incidenciaActualizada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidencia> obtenerPorId(@PathVariable Long id) {
        Incidencia incidencia = incidenciaService.obtenerPorId(id);
        return ResponseEntity.ok(incidencia);
    }

}
