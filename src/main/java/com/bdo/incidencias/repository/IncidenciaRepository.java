package com.bdo.incidencias.repository;

import com.bdo.incidencias.model.EstadoIncidencia;
import com.bdo.incidencias.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findByEstado(EstadoIncidencia estado);
}