DROP TABLE IF EXISTS incidencias;
DROP TABLE IF EXISTS usuarios;

-- Tabla Pdre: Usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

-- Tabla hija: Incidencias
CREATE TABLE incidencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion VARCHAR(1000),
    prioridad VARCHAR(10) NOT NULL CHECK(prioridad IN ('BAJA','MEDIA', 'ALTA')),
    estado VARCHAR(15) NOT NULL CHECK (estado IN ('ABIERTA','EN_CURSO', 'CERRADA')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    usuario_id BIGINT,
    CONSTRAINT fk_incidencias_usuario FOREIGN KEY(usuario_id)
    REFERENCES usuarios(id) ON DELETE SET NULL
);

-- 3. Datos de prueba iniciales
INSERT INTO usuarios (nombre, email) VALUES
('Roberto López', 'roberto.lopez@bdo.es'),
('Laura Gómez', 'laura.gomez@bdo.es');

INSERT INTO incidencias (titulo, descripcion, prioridad, estado, usuario_id)VALUES
('Error al conectar con VPN', 'Los usuarios remotos no pueden acceder a la red interna', 'ALTA', 'ABIERTA', 1),
('Impresora planta 2 sin tóner', 'La impresora del departamento contable no imprime', 'BAJA', 'EN_CURSO', 2),
('Fallo de inicio de sesión en ERP', 'Resuelto reiniciando el servicio de autenticación', 'MEDIA', 'CERRADA', 1);