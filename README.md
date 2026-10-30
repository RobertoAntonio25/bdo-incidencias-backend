# Sistema de Gestión de Incidencias Técnicas - BDO Spain

Aplicación web desarrollada como prueba técnica para la posición de **Desarrollador/a Java Middle**.
El proyecto implementa una API REST modular en **Spring Boot**, base de datos en memoria **H2** y un cliente web interactivo desarrollado con **HTML, CSS y JavaScript vanilla (Fetch API)** adaptado a la identidad corporativa de BDO.

---

## 1. Instrucciones de Arranque y Ejecución

La aplicación utiliza el Maven Wrapper (`mvnw`), por lo que no es necesario tener Maven instalado previamente en el equipo. Solo se requiere **Java 17 (JDK)**.

### Ejecución de la Aplicación (Un solo comando)

En Linux / macOS:

```bash
./mvnw spring-boot:run
```

En Windows (PowerShell / CMD):

```powershell
.\mvnw spring-boot:run
```

Una vez iniciada la aplicación:

- **Frontend Web:** Abre tu navegador en [http://localhost:8080](http://localhost:8080)
- **Consola H2 (Administrador de Base de Datos):** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:incidenciasdb`
  - Usuario: `sa`
  - Contraseña: _(en blanco)_

### Ejecución de Pruebas Unitarias (Un solo comando)

```bash
.\mvnw test
```

---

## 2. Decisiones Técnicas Principales y Justificación

1. **Arquitectura en Capas Limpias (Separation of Concerns):**
   - **Controlador (`@RestController`):** Responsable únicamente de la comunicación HTTP, serialización JSON y negociación de códigos de estado (201, 200, etc.).
   - **Servicio (`@Service`):** Contiene la lógica y reglas del negocio (`ConflictoNegocioException` para la regla de la incidencia cerrada). Permanece desacoplado del protocolo web.
   - **Acceso a Datos (`JpaRepository`):** Consultas derivadas declarativas como `findByEstado(...)` con proxies dinámicos de Spring Data.

2. **Uso de DTOs y Prevención de _Mass Assignment_:**
   - Separación estricta entre entidades JPA y contratos de transferencia (`CrearIncidenciaDto`, `CambiarEstadoDto`). Evita que un cliente altere campos sensibles (ID, fecha de creación o estado inicial).
   - Validación declarativa con `jakarta.validation` (`@NotBlank`, `@Size(max=100)`, `@NotNull`).

3. **Manejo Centralizado de Excepciones (`@RestControllerAdvice`):**
   - Retorna una estructura JSON uniforme (`ErrorResponse`) con `timestamp`, `status`, `error`, `mensaje` y lista de `detalles` para toda la API.

4. **Persistencia Determinista y Configuración H2:**
   - `spring.jpa.hibernate.ddl-auto=none` y `spring.sql.init.mode=always` para que el script `schema.sql` sea la única fuente de verdad (_Single Source of Truth_), garantizando claves foráneas y restricciones `CHECK`.
   - `DB_CLOSE_DELAY=-1` para evitar la pérdida del estado en memoria entre conexiones del pool HikariCP.

5. **Pruebas Unitarias Aisladas con Mockito:**
   - Uso de `@ExtendWith(MockitoExtension.class)` en lugar de `@SpringBootTest`. Ejecuta los tests en menos de medio segundo aislando la lógica sin cargar el contenedor web ni la base de datos.
   - Inclusión de `verify(..., never()).save(...)` para auditar efectos secundarios y evitar datos corruptos.

6. **Frontend Vanilla y Responsive:**
   - Sin frameworks pesados (React/Angular/Vue). Se emplea JavaScript moderno (`async/await`, Fetch API) y CSS con variables corporativas BDO (`#ED1A3B`, `#002B49`).

---

## 3. Puntos de Mejora / Trabajo Pendiente con Más Tiempo

- **Documentación OpenAPI / Swagger:** Integrar `springdoc-openapi` para generar automáticamente la documentación interactiva en `/swagger-ui.html`.
- **Seguridad y Autenticación:** Incorporar Spring Security con JWT para roles de soporte (`ADMIN`, `OPERADOR`).
- **Paginación y Ordenación:** Reemplazar `List<Incidencia>` por `Page<Incidencia>` (`Pageable`) para grandes volúmenes de incidencias.
- **Manejo de Transacciones de Lectura:** Implementar consultas optimizadas con `JOIN FETCH` para evitar posibles problemas de $N+1$ en consultas complejas.
- **Pipeline CI/CD:** Añadir un workflow de GitHub Actions que compile y ejecute los tests automáticamente en cada Pull Request.

---

## 4. Uso y Apoyo de Inteligencia Artificial

De acuerdo con las pautas de transparencia y las prácticas modernas de desarrollo, se ha utilizado asistencia de IA (LLMs) durante la elaboración de esta prueba técnica exclusivamente como herramienta de productividad.

Su uso se ha limitado a:

Aceleración en la escritura de código boilerplate (constructores, getters/setters y configuración base).

Generación de datos de prueba ficticios para la inicialización de la base de datos y los tests unitarios.

La arquitectura en capas, las decisiones de diseño (uso de DTOs, manejo de excepciones), las reglas de negocio y la autoría intelectual de la solución han sido diseñadas y desarrolladas íntegramente por el candidato.
