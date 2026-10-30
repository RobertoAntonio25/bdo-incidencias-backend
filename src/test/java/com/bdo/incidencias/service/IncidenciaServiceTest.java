package com.bdo.incidencias.service;

import com.bdo.incidencias.dto.CrearIncidenciaDto;
import com.bdo.incidencias.exception.ConflictoNegocioException;
import com.bdo.incidencias.model.EstadoIncidencia;
import com.bdo.incidencias.model.Incidencia;
import com.bdo.incidencias.model.Prioridad;
import com.bdo.incidencias.model.Usuario;
import com.bdo.incidencias.repository.IncidenciaRepository;
import com.bdo.incidencias.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidenciaServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private IncidenciaService incidenciaService;

    @Test
    @DisplayName("Debe crear una incidencia con estado inicial ABIERTA exitosamente")
    void debeCrearIncidenciaConEstadoInicialAbierta() {
        // GIVEN: Datos de entrada y mocks configurados
        Usuario usuarioMock = new Usuario(1L, "Carlos Mendoza", "carlos@bdo.es");
        CrearIncidenciaDto dto = new CrearIncidenciaDto(
                "Fallo en servidor",
                "El servidor no responde peticiones",
                Prioridad.ALTA,
                1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(incidenciaRepository.save(any(Incidencia.class))).thenAnswer(invocation -> {
            Incidencia incGuardada = invocation.getArgument(0);
            incGuardada.setId(10L);
            return incGuardada;
        });

        // WHEN: Ejecutamos la acción del servicio
        Incidencia resultado = incidenciaService.crearIncidencia(dto);

        // THEN: Validamos las aserciones
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Fallo en servidor", resultado.getTitulo());
        assertEquals(EstadoIncidencia.ABIERTA, resultado.getEstado(), "El estado inicial siempre debe ser ABIERTA");
        assertEquals(usuarioMock, resultado.getUsuario());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(incidenciaRepository, times(1)).save(any(Incidencia.class));
    }

    @Test
    @DisplayName("Debe lanzar ConflictoNegocioException (409) al intentar cambiar estado de una incidencia CERRADA")
    void debeLanzarExcepcionAlCambiarEstadoDeIncidenciaCerrada() {
        // GIVEN: Una incidencia que ya está CERRADA
        Incidencia incidenciaCerrada = new Incidencia();
        incidenciaCerrada.setId(1L);
        incidenciaCerrada.setTitulo("Incidencia antigua");
        incidenciaCerrada.setEstado(EstadoIncidencia.CERRADA);

        when(incidenciaRepository.findById(1L)).thenReturn(Optional.of(incidenciaCerrada));

        // WHEN & THEN: Intentar cambiar de CERRADA a EN_CURSO debe lanzar
        // ConflictoNegocioException
        ConflictoNegocioException excepcion = assertThrows(
                ConflictoNegocioException.class,
                () -> incidenciaService.cambiarEstado(1L, EstadoIncidencia.EN_CURSO),
                "Se esperaba ConflictoNegocioException al intentar reabrir o modificar una incidencia CERRADA");

        assertTrue(excepcion.getMessage().contains("CERRADA"));

        // Verificamos que NUNCA se haya persistido nada en el repositorio
        verify(incidenciaRepository, never()).save(any(Incidencia.class));
    }
}