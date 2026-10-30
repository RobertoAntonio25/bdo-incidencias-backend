package com.bdo.incidencias.service;

import com.bdo.incidencias.dto.CrearIncidenciaDto;
import com.bdo.incidencias.exception.ConflictoNegocioException;
import com.bdo.incidencias.exception.RecursoNoEncontradoException;
import com.bdo.incidencias.model.EstadoIncidencia;
import com.bdo.incidencias.model.Incidencia;
import com.bdo.incidencias.model.Usuario;
import com.bdo.incidencias.repository.IncidenciaRepository;
import com.bdo.incidencias.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;

    // Inyección de dependencias por constructor
    public IncidenciaService(IncidenciaRepository incidenciaRepository, UsuarioRepository usuarioRepository) {
        this.incidenciaRepository = incidenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Incidencia crearIncidencia(CrearIncidenciaDto dto) {
        Usuario usuario = null;
        if (dto.getUsuarioId() != null) {
            usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el usuario con ID: " + dto.getUsuarioId()));
        }

        // El estado inicial es siempre ABIERTA (requisito)
        Incidencia nuevaIncidencia = new Incidencia(
                dto.getTitulo(),
                dto.getDescripcion(),
                dto.getPrioridad(),
                EstadoIncidencia.ABIERTA,
                usuario);

        return incidenciaRepository.save(nuevaIncidencia);
    }

    @Transactional(readOnly = true)
    public List<Incidencia> listarIncidencias(EstadoIncidencia estado) {
        if (estado != null) {
            return incidenciaRepository.findByEstado(estado);
        }
        return incidenciaRepository.findAll();
    }

    @Transactional
    public Incidencia cambiarEstado(Long id, EstadoIncidencia nuevoEstado) {
        // 1. Validar existencia (404 si no existe)
        Incidencia incidencia = incidenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró la incidencia con ID: " + id));

        // 2. Regla de negocio innegociable: Incidencia CERRADA no se puede
        // modificar/reabrir (409)
        if (incidencia.getEstado() == EstadoIncidencia.CERRADA) {
            throw new ConflictoNegocioException(
                    "No se puede modificar una incidencia que ya se encuentra en estado CERRADA.");
        }

        incidencia.setEstado(nuevoEstado);
        return incidenciaRepository.save(incidencia);
    }
}