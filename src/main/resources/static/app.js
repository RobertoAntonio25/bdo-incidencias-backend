const API_BASE = "/api/incidencias";

// Elementos del DOM
const filtroEstado = document.getElementById("filtroEstado");
const cuerpoTabla = document.getElementById("cuerpoTabla");
const alerta = document.getElementById("alerta");
const mensajeAlerta = document.getElementById("mensajeAlerta");
const formCrearIncidencia = document.getElementById("formCrearIncidencia");

// Inicialización
document.addEventListener("DOMContentLoaded", () => {
  cargarIncidencias();

  // Evento de filtro reactivo (Requisito BDO: sin recargar página)
  filtroEstado.addEventListener("change", () => {
    cargarIncidencias(filtroEstado.value);
  });

  // Evento envío formulario de creación (Opcional)
  if (formCrearIncidencia) {
    formCrearIncidencia.addEventListener("submit", manejarCrearIncidencia);
  }
});

/**
 * 1. GET /api/incidencias: Lista incidencias con filtro opcional
 */
async function cargarIncidencias(estado = "", limpiarAlerta = true) {
  if (limpiarAlerta) {
    mostrarAlerta(false);
  }
  cuerpoTabla.innerHTML =
    '<tr><td colspan="7" class="text-center">Cargando incidencias...</td></tr>';

  try {
    const url = estado
      ? `${API_BASE}?estado=${encodeURIComponent(estado)}`
      : API_BASE;
    const response = await fetch(url);

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(
        errorData.mensaje ||
          `Error al obtener incidencias (Código ${response.status})`,
      );
    }

    const incidencias = await response.json();
    renderizarTabla(incidencias);
  } catch (error) {
    cuerpoTabla.innerHTML =
      '<tr><td colspan="7" class="text-center" style="color:var(--error-text);">No se pudieron cargar las incidencias.</td></tr>';
    mostrarAlerta(
      true,
      `Error al comunicarse con la API: ${error.message}`,
      "error",
    );
  }
}

/**
 * Renderiza la lista de incidencias en el tbody de la tabla
 */
function renderizarTabla(incidencias) {
  if (!incidencias || incidencias.length === 0) {
    cuerpoTabla.innerHTML =
      '<tr><td colspan="7" class="text-center" style="color:var(--bdo-text-muted);">No hay incidencias registradas para el filtro seleccionado.</td></tr>';
    return;
  }

  cuerpoTabla.innerHTML = incidencias
    .map((inc) => {
      const fechaFormateada = inc.fechaCreacion
        ? new Date(inc.fechaCreacion).toLocaleString("es-ES", {
            dateStyle: "short",
            timeStyle: "short",
          })
        : "N/A";
      const usuarioNombre = inc.usuario
        ? `${escaparHtml(inc.usuario.nombre)} (${escaparHtml(inc.usuario.email)})`
        : "<em>Sin asignar</em>";
      const estaCerrada = inc.estado === "CERRADA";

      return `
            <tr>
                <td><strong>#${inc.id}</strong></td>
                <td>
                    <div style="font-weight:600; color:var(--bdo-dark);">${escaparHtml(inc.titulo)}</div>
                    ${inc.descripcion ? `<small style="color:var(--bdo-text-muted);">${escaparHtml(inc.descripcion)}</small>` : ""}
                </td>
                <td><span class="badge badge-prioridad-${inc.prioridad}">${inc.prioridad}</span></td>
                <td><span class="badge badge-estado-${inc.estado}">${inc.estado}</span></td>
                <td>${usuarioNombre}</td>
                <td>${fechaFormateada}</td>
                <td>
                    <select class="select-cambiar-estado" 
                            onchange="manejarCambioEstado(${inc.id}, this.value, '${inc.estado}')"
                            ${estaCerrada ? 'disabled title="Una incidencia CERRADA no se puede modificar ni reabrir"' : ""}>
                        <option value="ABIERTA" ${inc.estado === "ABIERTA" ? "selected" : ""}>ABIERTA</option>
                        <option value="EN_CURSO" ${inc.estado === "EN_CURSO" ? "selected" : ""}>EN_CURSO</option>
                        <option value="CERRADA" ${inc.estado === "CERRADA" ? "selected" : ""}>CERRADA</option>
                    </select>
                </td>
            </tr>
        `;
    })
    .join("");
}

/**
 * 2. PATCH /api/incidencias/{id}/estado: Cambia el estado de una incidencia
 */
async function manejarCambioEstado(id, nuevoEstado, estadoAnterior) {
  if (nuevoEstado === estadoAnterior) return;

  mostrarAlerta(false);

  try {
    const response = await fetch(`${API_BASE}/${id}/estado`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ estado: nuevoEstado }),
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      // Manejo del 409 (Conflicto) y 404
      throw new Error(
        data.mensaje ||
          `Error ${response.status}: No se pudo actualizar el estado.`,
      );
    }

    mostrarAlerta(
      true,
      `Estado de la incidencia #${id} actualizado exitosamente a ${nuevoEstado}.`,
      "success",
    );
    cargarIncidencias(filtroEstado.value, false);
  } catch (error) {
    mostrarAlerta(true, error.message, "error");
    // Revertir recargando la tabla
    cargarIncidencias(filtroEstado.value, false);
  }
}

/**
 * 3. POST /api/incidencias: Crea una nueva incidencia
 */
async function manejarCrearIncidencia(e) {
  e.preventDefault();
  mostrarAlerta(false);

  const titulo = document.getElementById("titulo").value.trim();
  const descripcion = document.getElementById("descripcion").value.trim();
  const prioridad = document.getElementById("prioridad").value;
  const usuarioIdVal = document.getElementById("usuarioId").value;
  const usuarioId = usuarioIdVal ? parseInt(usuarioIdVal, 10) : null;

  const payload = {
    titulo,
    descripcion: descripcion || null,
    prioridad,
    usuarioId,
  };

  try {
    const response = await fetch(API_BASE, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      if (data.detalles && data.detalles.length > 0) {
        throw new Error(data.detalles.join(" | "));
      }
      throw new Error(
        data.mensaje || `Error ${response.status} al crear la incidencia.`,
      );
    }

    mostrarAlerta(
      true,
      `Incidencia #${data.id} creada correctamente con estado ABIERTA.`,
      "success",
    );
    formCrearIncidencia.reset();
    document.getElementById("prioridad").value = "MEDIA";
    cargarIncidencias(filtroEstado.value, false);
  } catch (error) {
    mostrarAlerta(true, error.message, "error");
  }
}

/**
 * Control visual de la alerta
 */
function mostrarAlerta(visible, texto = "", tipo = "error") {
  if (!visible) {
    alerta.className = "alerta oculta";
    mensajeAlerta.textContent = "";
    return;
  }

  alerta.className = `alerta ${tipo}`;
  mensajeAlerta.textContent = texto;
}

function cerrarAlerta() {
  mostrarAlerta(false);
}

function escaparHtml(texto) {
  if (!texto) return "";
  return texto
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}
