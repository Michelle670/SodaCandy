# Soda Candy — Sistema Web Transaccional de Gestión de Pedidos y Ventas

Proyecto Final · Aplicación Web con Java y Spring Boot
Universidad Fidelitas

---

## Descripción

Soda Candy es una aplicación web transaccional que digitaliza la operación de una soda familiar en Puriscal, San José. El sistema gestiona el menú, la toma de pedidos, la comunicación con cocina y el registro de ventas.

---

## Integrantes

- Fauricio Calderón Mejía
- Sheilyn Córdoba Bravo
- Michelle Guerrero Brenes
- Sofía Loaiza Cabalceta

## Ramas asignadas

| Integrante          | Rama                       |
|---------------------|----------------------------|
| Fauricio Calderon   | feature/ronald-pedidos     |
| Sheilyn Cordoba     | feature/sheilyn-salon      |
| Michelle Guerrero   | feature/michelle-menu      |
| Sofia Loaiza        | feature/sofia-cocina-cobro |

## Estado del avance por módulo

> **Leyenda:** Listo · En proceso · Pendiente
>
> _Última actualización: 06/07/2026_

---

### Módulo MENÚ — Michelle

- **HU-06** · CRUD de Categorías (crear, editar, eliminar; validación de no borrar si tiene productos asociados). — Listo
- **HU-07** · CRUD de Productos (nombre, precio no negativo, categoría, disponibilidad). — Listo
- **HU-08** · Marcar producto como *Agotado* (cambio dinámico de etiqueta de disponibilidad). — Listo
- **HU-09** · Vista de consulta del menú para el salonero (agrupado por categoría, con precios y disponibilidad). — Listo

### Módulo MESAS + EMPLEADOS — Sheilyn

- **HU-02** · Gestión de empleados (crear, editar, desactivar con rol asignado; sin duplicados). — Listo
- **HU-11** · Apertura de mesa (LIBRE → OCUPADA e inicialización del pedido en curso). — Listo
- **HU-15** · Panel de estado de las mesas en tiempo real (LIBRE / OCUPADA / POR_COBRAR). — Listo

### Módulo PEDIDOS — Ronald

- **HU-12** · Agregar ítems al pedido (producto y cantidad) con recálculo del total en vivo. — En proceso
- **HU-13** · Pedidos tipo `PARA_LLEVAR` (sin mesa, capturando nombre y teléfono). — En proceso
- **HU-14** · Modificación de ítems antes de enviar a cocina (cambiar cantidades, eliminar líneas y recalcular). — En proceso
- **HU-16** · Enviar pedido a cocina (cambio de estado a `EN_PREPARACION`). — En proceso
- **HU-21** · Notas por ítem (campo de texto por línea, con resaltado visual para alergias). — En proceso

### Módulo COCINA + COBRO — Sofía

- **HU-17** · Tablero de cocina con actualización automática (polling) de pedidos pendientes y sus notas. — Listo
- **HU-18** · Marcar pedido como *Listo* (cambio de estado, salida de la cola y registro de hora). — Listo
- **HU-19** · Registrar método de pago, cambiar estado a `PAGADO`, liberar la mesa y generar tiquete PDF. — Listo
- **HU-20** · Reporte de ventas del día (total vendido y productos más vendidos). — Pendiente

---

### Transversales (login e idioma)

- **HU-01** · Inicio de sesión con usuario y contraseña según rol. — Pendiente
- **HU-03** · Restricción por rol (cada rol ve solo sus funciones). — Pendiente
- **HU-04** · Cierre de sesión. — Listo
- **HU-05** · Cambio de idioma ES/EN (i18n). — Listo
- **HU-10** · Menú digital público para el cliente (vista sin login, responsiva). — Listo

---

**Resumen:** 14 de 21 HU listas · Módulo Pedidos (5 HU) en proceso · HU-01 y HU-03 pendientes (login diferido hasta Spring Security).

- La rama main es la rama principal del proyecto. Solo llega código revisado y aprobado por el equipo. Nadie sube cambios directamente a main.
Reglas del equipo

- Cada persona trabaja únicamente en su propia rama. Antes de empezar a trabajar siempre se debe actualizar la rama. Los mensajes de commit deben ser claros y describir qué se hizo. Cuando una tarea esté lista, se abre un Pull Request hacia main para que el equipo lo revise y apruebe.

## Pasos para trabajar cada día
-- Clonar el repositorio, solo la primera vez:
git clone [<URL-del-repositorio>](https://github.com/Michelle670/SodaCandy.git)
cd SodaCandy

-- Cambiarse a tu rama y actualizarla antes de empezar:
Ejemplo:
git checkout Michelle
git pull origin Michelle

-- Guardar y subir los cambios:
git add .
git commit -m "Descripción de lo que hice"
Ej: git push origin Michelle

- Cuando una tarea esté completa, ir a GitHub, abrir un Pull Request desde tu rama hacia main y avisar al equipo.
  
## Buenas practicas para los commits
Los mensajes deben indicar claramente que se hizo, por ejemplo: "Agrego formulario de login", "Corrijo error en la navegacion", "Creo componente de tabla de usuarios". Evitar mensajes vagos como "cambios" o "arreglos".

## Recordatorios
Si se borra algo por accidente Git guarda el historial, no hay que entrar en panico. Si hay un conflicto al hacer pull, avisar al equipo antes de resolverlo. Nunca usar git push --force sin consultar primero.
Cualquier cambio a este acuerdo debe ser aprobado por todos los integrantes del equipo.

## Objetivos

Digitalizar la gestión de pedidos y ventas de Soda Candy para contar con un registro confiable de las operaciones diarias.

- Digitalizar el menú con precios y categorías.
- Gestionar la toma y comunicación de pedidos entre salón y cocina.
- Registrar ventas y generar información útil para la toma de decisiones.

---

## Tecnologías

| Herramienta          | Uso                        |
|----------------------|----------------------------|
| Java 21              | Lenguaje principal         |
| Spring Boot          | Framework backend          |
| NetBeans 29          | IDE de desarrollo          |
| MySQL + Workbench    | Base de datos              |
| Render               | Despliegue en la nube      |
| Git + GitHub         | Control de versiones       |

---

## Configuración local

La base de datos se maneja localmente con MySQL Workbench durante el desarrollo. Las instrucciones de conexión y configuración se documentarán conforme avance el proyecto.

---

