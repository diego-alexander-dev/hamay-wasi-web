-- =====================================================================
-- Hamay Wasi - Esquema PostgreSQL (fuente de verdad del proyecto)
-- Base: hamay_wasi_avanzada
-- Ejecutar una sola vez, con la base vacia:
--   psql -U postgres -d hamay_wasi_avanzada -f docs/hamay_wasi_avanzada.sql
-- Hibernate usa ddl-auto=validate: NUNCA crea ni modifica tablas.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. CATALOGOS
-- ---------------------------------------------------------------------

CREATE TABLE tipos_habitacion (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre        VARCHAR(120)  NOT NULL UNIQUE,
    descripcion   TEXT,
    tamano_m2     SMALLINT      NOT NULL,
    cama          VARCHAR(60)   NOT NULL,
    capacidad     SMALLINT      NOT NULL DEFAULT 2,
    precio_noche  NUMERIC(10,2) NOT NULL,
    imagen_url    VARCHAR(500),
    activo        BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_tipo_precio    CHECK (precio_noche > 0),
    CONSTRAINT ck_tipo_capacidad CHECK (capacidad BETWEEN 1 AND 10),
    CONSTRAINT ck_tipo_tamano    CHECK (tamano_m2 > 0)
);

CREATE TABLE habitaciones (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    numero   VARCHAR(10) NOT NULL UNIQUE,
    piso     SMALLINT    NOT NULL,
    tipo_id  BIGINT      NOT NULL REFERENCES tipos_habitacion(id),
    estado   VARCHAR(15) NOT NULL DEFAULT 'DISPONIBLE',
    CONSTRAINT ck_hab_estado CHECK (estado IN ('DISPONIBLE','OCUPADA','LIMPIEZA','MANTENIMIENTO'))
);
CREATE INDEX idx_hab_tipo   ON habitaciones (tipo_id);
CREATE INDEX idx_hab_estado ON habitaciones (estado);

CREATE TABLE servicios (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre  VARCHAR(80)   NOT NULL UNIQUE,
    precio  NUMERIC(10,2) NOT NULL,
    activo  BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_serv_precio CHECK (precio >= 0)
);

CREATE TABLE promociones (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo                VARCHAR(30)  NOT NULL UNIQUE,
    descripcion           VARCHAR(200),
    porcentaje_descuento  NUMERIC(5,2) NOT NULL,
    vigente_desde         DATE NOT NULL,
    vigente_hasta         DATE NOT NULL,
    activo                BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_promo_pct    CHECK (porcentaje_descuento > 0 AND porcentaje_descuento <= 100),
    CONSTRAINT ck_promo_fechas CHECK (vigente_hasta >= vigente_desde)
);

-- ---------------------------------------------------------------------
-- 2. PERSONAS Y USUARIOS
-- ---------------------------------------------------------------------

CREATE TABLE huespedes (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres           VARCHAR(80)  NOT NULL,
    apellidos         VARCHAR(80)  NOT NULL,
    tipo_documento    VARCHAR(10)  NOT NULL DEFAULT 'DNI',
    numero_documento  VARCHAR(20)  NOT NULL,
    email             VARCHAR(120) NOT NULL,
    telefono          VARCHAR(30),
    pais              VARCHAR(60)  DEFAULT 'Perú',
    creado_en         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_huesped_doc CHECK (tipo_documento IN ('DNI','CE','PASAPORTE')),
    CONSTRAINT uq_huesped_doc UNIQUE (tipo_documento, numero_documento)
);
CREATE INDEX idx_huesped_email  ON huespedes (email);
CREATE INDEX idx_huesped_nombre ON huespedes (apellidos, nombres);



-- ---------------------------------------------------------------------
-- 3. RESERVAS
-- ---------------------------------------------------------------------

CREATE TABLE reservas (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo          VARCHAR(20) NOT NULL UNIQUE,              -- ej: HW-K7M2QX
    huesped_id      BIGINT NOT NULL REFERENCES huespedes(id),
    promocion_id    BIGINT NULL     REFERENCES promociones(id),
    fecha_entrada   DATE NOT NULL,
    fecha_salida    DATE NOT NULL,
    num_huespedes   SMALLINT NOT NULL DEFAULT 1,
    estado          VARCHAR(15) NOT NULL DEFAULT 'CONFIRMADA',
    observaciones   VARCHAR(500),
    total           NUMERIC(12,2) NOT NULL DEFAULT 0,
    creado_en       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_res_estado CHECK (estado IN ('PENDIENTE','CONFIRMADA','CHECK_IN','CHECK_OUT','CANCELADA')),
    CONSTRAINT ck_res_fechas CHECK (fecha_salida > fecha_entrada),
    CONSTRAINT ck_res_huesp  CHECK (num_huespedes BETWEEN 1 AND 20)
);
CREATE INDEX idx_res_fechas  ON reservas (fecha_entrada, fecha_salida);
CREATE INDEX idx_res_estado  ON reservas (estado);
CREATE INDEX idx_res_huesped ON reservas (huesped_id);

-- Una reserva puede incluir varias habitaciones. Se guarda el precio del momento
-- para que un cambio de tarifa no altere reservas pasadas.
CREATE TABLE reserva_habitaciones (
    reserva_id     BIGINT NOT NULL REFERENCES reservas(id) ON DELETE CASCADE,
    habitacion_id  BIGINT NOT NULL REFERENCES habitaciones(id),
    precio_noche   NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (reserva_id, habitacion_id)
);
CREATE INDEX idx_rh_hab ON reserva_habitaciones (habitacion_id);

CREATE TABLE reserva_servicios (
    reserva_id       BIGINT NOT NULL REFERENCES reservas(id) ON DELETE CASCADE,
    servicio_id      BIGINT NOT NULL REFERENCES servicios(id),
    cantidad         SMALLINT NOT NULL DEFAULT 1,
    precio_unitario  NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (reserva_id, servicio_id),
    CONSTRAINT ck_rs_cant CHECK (cantidad > 0)
);

CREATE TABLE pagos (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reserva_id  BIGINT NOT NULL REFERENCES reservas(id) ON DELETE CASCADE,
    monto       NUMERIC(12,2) NOT NULL,
    metodo      VARCHAR(15) NOT NULL,
    estado      VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE',
    referencia  VARCHAR(60),
    pagado_en   TIMESTAMP NULL,
    creado_en   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_pago_monto  CHECK (monto > 0),
    CONSTRAINT ck_pago_metodo CHECK (metodo IN ('EFECTIVO','TARJETA','YAPE','PLIN','TRANSFERENCIA')),
    CONSTRAINT ck_pago_estado CHECK (estado IN ('PENDIENTE','PAGADO','REEMBOLSADO'))
);
CREATE INDEX idx_pago_reserva ON pagos (reserva_id);

-- ---------------------------------------------------------------------
-- 4. CONTACTO Y AUDITORIA
-- ---------------------------------------------------------------------

CREATE TABLE mensajes_contacto (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre     VARCHAR(80)  NOT NULL,
    apellidos  VARCHAR(80)  NOT NULL,
    email      VARCHAR(120) NOT NULL,
    telefono   VARCHAR(30),
    mensaje    TEXT NOT NULL,
    leido      BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_msg_leido ON mensajes_contacto (leido);

CREATE TABLE auditoria_reservas (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reserva_id  BIGINT NOT NULL,
    accion      VARCHAR(20)  NOT NULL,
    detalle     VARCHAR(300),
    usuario_bd  VARCHAR(80)  NOT NULL DEFAULT current_user,
    fecha       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_aud_reserva ON auditoria_reservas (reserva_id);

-- ---------------------------------------------------------------------
-- 5. TRIGGERS (en PostgreSQL cada trigger usa una funcion)
-- ---------------------------------------------------------------------

-- Impide reservar una habitacion que ya esta ocupada en esas fechas
CREATE OR REPLACE FUNCTION fn_rh_sin_cruce() RETURNS trigger
LANGUAGE plpgsql AS $$
DECLARE
    v_entrada DATE;
    v_salida  DATE;
    v_choques INT;
BEGIN
    SELECT fecha_entrada, fecha_salida INTO v_entrada, v_salida
      FROM reservas WHERE id = NEW.reserva_id;

    SELECT COUNT(*) INTO v_choques
      FROM reserva_habitaciones rh
      JOIN reservas r ON r.id = rh.reserva_id
     WHERE rh.habitacion_id = NEW.habitacion_id
       AND r.id <> NEW.reserva_id
       AND r.estado IN ('PENDIENTE','CONFIRMADA','CHECK_IN')
       AND r.fecha_entrada < v_salida
       AND r.fecha_salida  > v_entrada;

    IF v_choques > 0 THEN
        RAISE EXCEPTION 'La habitación ya está reservada en esas fechas';
    END IF;
    RETURN NEW;
END $$;

CREATE TRIGGER trg_rh_sin_cruce
BEFORE INSERT ON reserva_habitaciones
FOR EACH ROW EXECUTE FUNCTION fn_rh_sin_cruce();

-- Actualiza "actualizado_en" en cada modificacion (PostgreSQL no lo hace solo)
CREATE OR REPLACE FUNCTION fn_reservas_touch() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    NEW.actualizado_en := CURRENT_TIMESTAMP;
    RETURN NEW;
END $$;

CREATE TRIGGER trg_reservas_touch
BEFORE UPDATE ON reservas
FOR EACH ROW EXECUTE FUNCTION fn_reservas_touch();

-- Registra cada cambio de estado
CREATE OR REPLACE FUNCTION fn_reservas_auditar_update() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    IF OLD.estado <> NEW.estado THEN
        INSERT INTO auditoria_reservas (reserva_id, accion, detalle)
        VALUES (NEW.id, 'CAMBIO_ESTADO', OLD.estado || ' -> ' || NEW.estado);
    END IF;
    RETURN NEW;
END $$;

CREATE TRIGGER trg_reservas_auditar_update
AFTER UPDATE ON reservas
FOR EACH ROW EXECUTE FUNCTION fn_reservas_auditar_update();

-- Registra las reservas eliminadas
CREATE OR REPLACE FUNCTION fn_reservas_auditar_delete() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO auditoria_reservas (reserva_id, accion, detalle)
    VALUES (OLD.id, 'ELIMINADA', 'Codigo ' || OLD.codigo || ', total ' || OLD.total);
    RETURN OLD;
END $$;

CREATE TRIGGER trg_reservas_auditar_delete
AFTER DELETE ON reservas
FOR EACH ROW EXECUTE FUNCTION fn_reservas_auditar_delete();

-- ---------------------------------------------------------------------
-- 6. FUNCIONES Y PROCEDIMIENTOS
-- ---------------------------------------------------------------------

-- Habitaciones libres entre dos fechas (p_tipo NULL = todos los tipos)
-- Uso:  SELECT * FROM fn_habitaciones_disponibles(CURRENT_DATE, CURRENT_DATE + 2);
CREATE OR REPLACE FUNCTION fn_habitaciones_disponibles(
    p_entrada DATE, p_salida DATE, p_tipo BIGINT DEFAULT NULL)
RETURNS TABLE (hab_id BIGINT, hab_numero VARCHAR, hab_piso SMALLINT,
               tipo_nombre VARCHAR, precio NUMERIC)
LANGUAGE sql STABLE AS $$
    SELECT h.id, h.numero, h.piso, t.nombre, t.precio_noche
      FROM habitaciones h
      JOIN tipos_habitacion t ON t.id = h.tipo_id
     WHERE h.estado <> 'MANTENIMIENTO'
       AND t.activo = TRUE
       AND (p_tipo IS NULL OR h.tipo_id = p_tipo)
       AND NOT EXISTS (
            SELECT 1
              FROM reserva_habitaciones rh
              JOIN reservas r ON r.id = rh.reserva_id
             WHERE rh.habitacion_id = h.id
               AND r.estado IN ('PENDIENTE','CONFIRMADA','CHECK_IN')
               AND r.fecha_entrada < p_salida
               AND r.fecha_salida  > p_entrada)
     ORDER BY t.precio_noche, h.numero;
$$;

-- Recalcula el total = (habitaciones x noches + servicios) - descuento
-- Uso:  CALL sp_recalcular_total(1);
CREATE OR REPLACE PROCEDURE sp_recalcular_total(p_reserva BIGINT)
LANGUAGE plpgsql AS $$
DECLARE
    v_noches INT           := 0;
    v_desc   NUMERIC(5,2)  := 0;
    v_hab    NUMERIC(12,2) := 0;
    v_srv    NUMERIC(12,2) := 0;
BEGIN
    SELECT (r.fecha_salida - r.fecha_entrada), COALESCE(p.porcentaje_descuento, 0)
      INTO v_noches, v_desc
      FROM reservas r
      LEFT JOIN promociones p ON p.id = r.promocion_id
     WHERE r.id = p_reserva;

    SELECT COALESCE(SUM(precio_noche), 0) * v_noches INTO v_hab
      FROM reserva_habitaciones WHERE reserva_id = p_reserva;

    SELECT COALESCE(SUM(cantidad * precio_unitario), 0) INTO v_srv
      FROM reserva_servicios WHERE reserva_id = p_reserva;

    UPDATE reservas
       SET total = ROUND((v_hab + v_srv) * (1 - v_desc / 100), 2)
     WHERE id = p_reserva;
END $$;

-- ---------------------------------------------------------------------
-- 7. VISTAS
-- ---------------------------------------------------------------------

CREATE VIEW v_reservas_detalle AS
SELECT r.id, r.codigo,
       g.nombres || ' ' || g.apellidos AS huesped,
       g.email, g.telefono,
       r.fecha_entrada, r.fecha_salida,
       (r.fecha_salida - r.fecha_entrada) AS noches,
       r.num_huespedes, r.estado, r.total,
       COALESCE(pg.pagado, 0)           AS pagado,
       r.total - COALESCE(pg.pagado, 0) AS saldo,
       string_agg(DISTINCT h.numero, ', ' ORDER BY h.numero)  AS habitaciones,
       string_agg(DISTINCT t.nombre, ' | ' ORDER BY t.nombre) AS tipos
  FROM reservas r
  JOIN huespedes g ON g.id = r.huesped_id
  LEFT JOIN reserva_habitaciones rh ON rh.reserva_id = r.id
  LEFT JOIN habitaciones h ON h.id = rh.habitacion_id
  LEFT JOIN tipos_habitacion t ON t.id = h.tipo_id
  LEFT JOIN (SELECT reserva_id, SUM(monto) AS pagado
               FROM pagos WHERE estado = 'PAGADO' GROUP BY reserva_id) pg
         ON pg.reserva_id = r.id
 GROUP BY r.id, g.id, pg.pagado;

CREATE VIEW v_movimiento_hoy AS
SELECT 'LLEGADA' AS movimiento, codigo, huesped, habitaciones, fecha_entrada AS fecha
  FROM v_reservas_detalle
 WHERE fecha_entrada = CURRENT_DATE AND estado IN ('CONFIRMADA','PENDIENTE')
UNION ALL
SELECT 'SALIDA', codigo, huesped, habitaciones, fecha_salida
  FROM v_reservas_detalle
 WHERE fecha_salida = CURRENT_DATE AND estado = 'CHECK_IN';

CREATE VIEW v_ingresos_mensuales AS
SELECT to_char(fecha_entrada, 'YYYY-MM') AS mes,
       COUNT(*)   AS reservas,
       SUM(total) AS total
  FROM reservas
 WHERE estado <> 'CANCELADA'
 GROUP BY to_char(fecha_entrada, 'YYYY-MM')
 ORDER BY mes;

-- ---------------------------------------------------------------------
-- 8. DATOS DE EJEMPLO (precios y datos inventados: cambialos a los reales)
-- ---------------------------------------------------------------------

INSERT INTO tipos_habitacion (nombre, descripcion, tamano_m2, cama, capacidad, precio_noche) VALUES
('Standard Individual con Vista a la Calle',  'Ambiente clásico y cálido con vista a la ciudad.',  18, 'Cama king-size', 1, 120.00),
('Standard Individual con Vista Interior',    'Silencio y tranquilidad en el corazón del hotel.',   18, 'Cama king-size', 1, 110.00),
('Standard Matrimonial con Vista a la Calle', 'Amplitud y confort para parejas.',                   22, 'Cama king-size', 2, 160.00),
('Standard Doble Dos Camas',                  'Ideal para amigos o familias pequeñas.',             25, '2 Camas Queen',  4, 200.00);

INSERT INTO habitaciones (numero, piso, tipo_id) VALUES
('101', 1, 1), ('102', 1, 1),
('103', 1, 2), ('104', 1, 2),
('201', 2, 3), ('202', 2, 3),
('203', 2, 4), ('204', 2, 4);

INSERT INTO servicios (nombre, precio) VALUES
('Desayuno buffet', 25.00),
('Traslado desde el terminal', 30.00),
('Lavandería', 15.00),
('Tour por el valle', 80.00);

INSERT INTO promociones (codigo, descripcion, porcentaje_descuento, vigente_desde, vigente_hasta) VALUES
('BIENVENIDO10', '10% de descuento en tu primera reserva', 10.00, CURRENT_DATE, CURRENT_DATE + 365);

INSERT INTO huespedes (nombres, apellidos, tipo_documento, numero_documento, email, telefono) VALUES
('Rosa',  'Huamán Ccora',  'DNI', '40000001', 'rosa.huaman@example.com', '969111222'),
('Luis',  'Tovar Baldeón', 'DNI', '40000002', 'luis.tovar@example.com',  '938555010'),
('Carla', 'Rojas Paucar',  'DNI', '40000003', 'carla.rojas@example.com', '999000321');

INSERT INTO reservas (codigo, huesped_id, promocion_id, fecha_entrada, fecha_salida, num_huespedes, estado) VALUES
('HW-DEMO01', 1, NULL, CURRENT_DATE + 3,  CURRENT_DATE + 6,  2, 'CONFIRMADA'),
('HW-DEMO02', 2, 1,    CURRENT_DATE + 10, CURRENT_DATE + 12, 1, 'CONFIRMADA'),
('HW-DEMO03', 3, NULL, CURRENT_DATE + 21, CURRENT_DATE + 25, 4, 'PENDIENTE');

INSERT INTO reserva_habitaciones (reserva_id, habitacion_id, precio_noche) VALUES
(1, 5, 160.00), (2, 1, 120.00), (3, 7, 200.00);

INSERT INTO reserva_servicios (reserva_id, servicio_id, cantidad, precio_unitario) VALUES
(1, 1, 6, 25.00), (3, 4, 4, 80.00);

CALL sp_recalcular_total(1);
CALL sp_recalcular_total(2);
CALL sp_recalcular_total(3);

INSERT INTO pagos (reserva_id, monto, metodo, estado, pagado_en) VALUES
(1, 200.00, 'YAPE', 'PAGADO', CURRENT_TIMESTAMP);
