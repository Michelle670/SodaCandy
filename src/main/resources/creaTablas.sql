/*
  Script de creacion de base de datos para Soda Candy (SC-403)
  Restaurante familiar - Puriscal
  Este script crea el esquema, tablas, usuarios y carga datos de ejemplo.
  Modelo alineado con el Avance 1 (9 entidades, 3 roles), aprobado por el profe.

  Usuarios de la aplicacion sembrados aqui (para cuando exista login):
     admin    / Admin123.      (rol ADMINISTRADOR)
     salonero / Salonero123.   (rol SALONERO)
     cocina   / Cocina123.     (rol COCINA)
  Las claves se guardan con hash BCrypt, listas para cuando se agregue la seguridad.
*/
-- Seccion de administracion (ejecutar una vez en un entorno de desarrollo)
drop database if exists soda_candy;
drop user if exists usuario_prueba;
drop user if exists usuario_reportes;

-- Creacion del esquema
CREATE database soda_candy
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Creacion de usuarios de la BD (no confundir con los usuarios de la app)
create user 'usuario_prueba'@'%'   identified by 'Usuar1o_Clave.';
create user 'usuario_reportes'@'%' identified by 'Usuar1o_Reportes.';

-- Asignacion de permisos (especificos, no todos los privilegios)
grant select, insert, update, delete on soda_candy.* to 'usuario_prueba'@'%';
grant select on soda_candy.* to 'usuario_reportes'@'%';
flush privileges;

use soda_candy;

-- =========================================================
-- Seccion de Creacion de Tablas
-- =========================================================

-- Tabla de roles (catalogo: ADMINISTRADOR, SALONERO, COCINA)
create table rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol varchar(20) NOT NULL UNIQUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rol),
  index ndx_rol (rol))
  ENGINE = InnoDB;

-- Tabla de usuarios (empleados; los crea el administrador)
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username varchar(30) NOT NULL UNIQUE,
  password varchar(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellidos VARCHAR(30) NOT NULL,
  correo VARCHAR(75) NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  ruta_imagen varchar(1024),
  activo boolean,
  token_reset varchar(64) NULL,
  token_reset_expira DATETIME NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  CHECK (correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  index ndx_username (username))
  ENGINE = InnoDB;

-- Tabla puente entre usuarios y roles (llave compuesta)
create table usuario_rol (
  id_usuario INT NOT NULL,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario, id_rol),
  foreign key fk_usuarioRol_usuario (id_usuario) references usuario(id_usuario),
  foreign key fk_usuarioRol_rol (id_rol) references rol(id_rol))
  ENGINE = InnoDB;

-- Tabla de categorias del menu
create table categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(50) NOT NULL,
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_categoria),
  unique (descripcion),
  index ndx_categoria_descripcion (descripcion))
  ENGINE = InnoDB;

-- Tabla de productos (platillos del menu)
create table producto (
  id_producto INT NOT NULL AUTO_INCREMENT,
  id_categoria INT NOT NULL,
  descripcion VARCHAR(50) NOT NULL,
  detalle text,
  precio decimal(12,2) CHECK (precio >= 0),
  disponible boolean,
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_producto),
  unique (descripcion),
  index ndx_producto_descripcion (descripcion),
  foreign key fk_producto_categoria (id_categoria) references categoria(id_categoria))
  ENGINE = InnoDB;

-- Tabla de mesas del local (estados segun HU-15: LIBRE / OCUPADA / POR_COBRAR)
create table mesa (
  id_mesa INT NOT NULL AUTO_INCREMENT,
  numero INT NOT NULL UNIQUE CHECK (numero >= 1),
  capacidad INT CHECK (capacidad >= 1),
  estado ENUM('LIBRE','OCUPADA','POR_COBRAR') NOT NULL DEFAULT 'LIBRE',
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_mesa),
  index ndx_mesa_numero (numero))
  ENGINE = InnoDB;

-- Tabla de pedidos (encabezado: tipo, estado, fecha, total)
-- HU-13: el cliente del 'para llevar' se guarda como campos del propio pedido (no hay tabla cliente)
create table pedido (
  id_pedido INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_mesa INT NULL,
  tipo ENUM('MESA','PARA_LLEVAR') NOT NULL DEFAULT 'MESA',
  nombre_cliente VARCHAR(100) NULL,
  telefono VARCHAR(25) NULL,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  estado ENUM('PENDIENTE','EN_PREPARACION','SERVIDO','PAGADO','ANULADO') NOT NULL DEFAULT 'PENDIENTE',
  total decimal(12,2) CHECK (total >= 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_pedido),
  index ndx_pedido_usuario (id_usuario),
  index ndx_pedido_mesa (id_mesa),
  foreign key fk_pedido_usuario (id_usuario) references usuario(id_usuario),
  foreign key fk_pedido_mesa (id_mesa) references mesa(id_mesa))
  ENGINE = InnoDB;

-- Tabla de detalle del pedido (HU-21: nota por linea para alergias/modificaciones)
create table detalle_pedido (
  id_detalle INT NOT NULL AUTO_INCREMENT,
  id_pedido INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad int unsigned CHECK (cantidad > 0),
  precio_historico decimal(12,2) CHECK (precio_historico >= 0),
  nota varchar(255),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_detalle),
  index ndx_detalle_pedido (id_pedido),
  index ndx_detalle_producto (id_producto),
  foreign key fk_detalle_pedido (id_pedido) references pedido(id_pedido),
  foreign key fk_detalle_producto (id_producto) references producto(id_producto))
  ENGINE = InnoDB;

-- Tabla de pagos (entidad transaccional del Avance 1: un solo pago por pedido)
create table pago (
  id_pago INT NOT NULL AUTO_INCREMENT,
  id_pedido INT NOT NULL UNIQUE,
  monto decimal(12,2) NOT NULL CHECK (monto > 0),
  metodo ENUM('EFECTIVO','TARJETA','SINPE_MOVIL') NOT NULL,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_pago),
  index ndx_pago_pedido (id_pedido),
  foreign key fk_pago_pedido (id_pedido) references pedido(id_pedido))
  ENGINE = InnoDB;

-- =========================================================
-- Seccion de Insercion de Datos
-- =========================================================

-- Roles (segun Avance 1)
insert into rol (rol) values ('ADMINISTRADOR'), ('SALONERO'), ('COCINA');

-- Usuarios (claves BCrypt; texto plano en el comentario de cabecera)
INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, activo) VALUES
('admin',    '$2a$10$dD0zUeJC3/jqJVuQd2uYHeeJ0ETJ4Ke./32HTGQPr665Q/EjGqxY2', 'Ana',    'Administradora', 'admin@sodacandy.cr',    '8888-0001', true),
('salonero', '$2a$10$Y5Vh90e.36C.CrAcpHcSCeCmsq6HjjPGbq.8a2G453LVxWFr4thOO', 'Sergio', 'Salonero Mora',  'salonero@sodacandy.cr', '8888-0002', true),
('cocina',   '$2a$10$00ZNC1PZ40jFB7X/hguweOQHrVl0wDhaqfaqCbV2oWbLA.WNwGLUO', 'Carmen', 'Cocina Soto',    'cocina@sodacandy.cr',   '8888-0003', true);

-- Asignacion de roles a usuarios
insert into usuario_rol (id_usuario, id_rol) values
 (1,1),  -- admin    -> ADMINISTRADOR
 (2,2),  -- salonero -> SALONERO
 (3,3);  -- cocina   -> COCINA

-- Categorias del menu (menu real de Soda Candy)
INSERT INTO categoria (descripcion, ruta_imagen, activo) VALUES
('Casados',           null, true),
('Frescos Naturales', null, true),
('Empanadas',         null, true),
('Sandwiches',        null, true),
('Olla de Carne',     null, true),
('Pollo Caribeño',    null, true);

-- Productos (precios en colones)
INSERT INTO producto (id_categoria, descripcion, detalle, precio, disponible, activo) VALUES
(1, 'Casado de Pollo',          'Arroz, frijoles, ensalada, plátano maduro y pollo',          3500, true, true),
(1, 'Casado de Carne en Salsa', 'Arroz, frijoles, ensalada, plátano maduro y carne en salsa', 3800, true, true),
(1, 'Casado de Pescado',        'Arroz, frijoles, ensalada, plátano maduro y pescado',        4200, true, true),
(2, 'Fresco de Cas',            'Fresco natural de cas en agua',                              1200, true, true),
(2, 'Fresco de Tamarindo',      'Fresco natural de tamarindo en agua',                        1200, true, true),
(2, 'Agua de Sapo',             'Especialidad de la casa: jengibre con tapa de dulce',        1500, true, true),
(3, 'Empanada de Carne',        'Empanada de maíz rellena de carne',                           800, true, true),
(3, 'Empanada de Pollo',        'Empanada de maíz rellena de pollo',                           800, true, true),
(3, 'Empanada de Queso',        'Empanada de maíz rellena de queso',                           700, true, true),
(4, 'Sandwich de Pollo',        'Pan, pollo, vegetales frescos y salsa',                      2800, true, true),
(4, 'Sandwich Cubano',          'Pan, jamón, cerdo, queso y pepinillos',                      3200, true, true),
(5, 'Olla de Carne',            'Especialidad de los domingos: caldo con carne y verduras',   4500, true, true),
(6, 'Pollo Caribeño',           'Pollo caribeño con arroz con coco',                          4800, true, true);

-- Mesas (10 mesas, todas inician LIBRE)
INSERT INTO mesa (numero, capacidad, estado, activo) VALUES
(1,  4, 'LIBRE', true),
(2,  4, 'LIBRE', true),
(3,  4, 'LIBRE', true),
(4,  4, 'LIBRE', true),
(5,  6, 'LIBRE', true),
(6,  6, 'LIBRE', true),
(7,  4, 'LIBRE', true),
(8,  2, 'LIBRE', true),
(9,  2, 'LIBRE', true),
(10, 4, 'LIBRE', true);

-- Pedido 1: en MESA (mesa 3), tomado por el salonero, ya cobrado
INSERT INTO pedido (id_usuario, id_mesa, tipo, nombre_cliente, telefono, estado, total) VALUES
(2, 3, 'MESA', NULL, NULL, 'PAGADO', 8500);

-- Pedido 2: PARA_LLEVAR (mandadito), con nombre y telefono del cliente (HU-13)
INSERT INTO pedido (id_usuario, id_mesa, tipo, nombre_cliente, telefono, estado, total) VALUES
(2, NULL, 'PARA_LLEVAR', 'Doña Marta', '8888-7777', 'PAGADO', 3100);

-- Detalle de los pedidos (incluye notas HU-21)
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_historico, nota) VALUES
(1, 1, 1, 3500, 'Sin frijoles, alergia'),
(1, 2, 1, 3800, NULL),
(1, 4, 1, 1200, 'Sin hielo'),
(2, 7, 2,  800, NULL),
(2, 6, 1, 1500, 'Bien fría');

-- Pagos (un solo metodo por pedido)
INSERT INTO pago (id_pedido, monto, metodo) VALUES
(1, 8500, 'EFECTIVO'),
(2, 3100, 'SINPE_MOVIL');