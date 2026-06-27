# Soda Candy — Reparto de trabajo (Avance 2)

**Curso:** SC-403 · Universidad Fidélitas
**Equipo:** Ronald Calderón · Sheilyn Córdoba · Michelle Guerrero · Sofía Loaiza
**Entrega:** Avance 2 — Semana 9 (50% funcional, 10% de la nota final)

---

## 1. De dónde partimos (base compartida, ya hecha)

El proyecto ya tiene un **andamio** Spring Boot funcionando. **Nadie arranca de cero.** Lo que ya está listo:

- Estructura en capas (Controller / Service / Repository / Domain / vistas Thymeleaf).
- `pom.xml` (Spring Boot 4.0.6, Java 21), `application.properties` y conexión a MySQL con JPA.
- **Las 9 entidades del modelo + sus 9 repositorios** ya creados (el modelo compila).
- **`Rol` cableado de punta a punta** = la **plantilla a copiar** para todo lo demás.
- `creaTablas.sql` con la base `soda_candy` y **datos de prueba** (menú, mesas, pedidos de ejemplo).
- Fragmentos Thymeleaf + **Bootstrap** (head, navbar, footer, toasts).
- **HU-05** (idioma ES/EN) ✅ y **HU-10** (menú público) ✅ ya funcionando.

> Gracias a los datos seed (productos, mesas, etc.), **los 4 pueden trabajar en paralelo desde el día 1**, sin esperarse entre sí.

---

## 2. Regla de oro: cada quien copia el patrón de `Rol`

Para **cada entidad** que les toque, repitan exactamente lo que ya está hecho en `Rol`. El `domain` y el `repository` **ya existen**; solo hay que crear:

1. **Service** → copien `RolService.java`, renómbrenlo y cambien la entidad/repositorio.
2. **Controller** → copien `RolController.java`, ajusten `@RequestMapping("/loquesea")` y los atributos del `Model`.
3. **Vistas** → creen la carpeta `templates/loquesea/` y copien los 3 archivos de `rol/`:
   - `fragmentos.html` con los 4 fragmentos: `agregar` (modal), `listado` (tabla), `editar` (form), `confirmarEliminar` (modal).
   - `listado.html` y `modifica.html` (wrappers de `<section th:replace=...>`).
4. **Textos i18n** → agreguen sus llaves en `messages_es.properties` **y** `messages_en.properties`.
5. **Navbar** → agreguen el enlace a su pantalla en `general/fragmentos.html`.

> ⚠️ **Importante (rúbrica 8.3):** si alguien usa una estructura o un estilo distinto al del curso, o mete Bootstrap de otra forma, el avance se va a **cero**. Por eso TODOS copian el patrón de `Rol` y usan los fragmentos/Bootstrap que ya están.

---

## 3. Qué le toca a cada quien

### 🟦 Michelle — Módulo MENÚ
**Rama:** `feature/menu` · **Entidades:** `Categoria`, `Producto`

| HU | Qué hacer |
|----|-----------|
| **HU-06** | CRUD de **Categorías** (crear/editar/eliminar; no borrar categoría con productos). |
| **HU-07** | CRUD de **Productos** (nombre, precio, categoría, disponibilidad; precio no negativo). |
| **HU-08** | Marcar producto **agotado** (botón que cambia `disponible`; etiqueta visible). |
| **HU-09** | Vista de **consulta del menú para el salonero** (agrupado por categoría, con precios y disponibilidad). |

**Tarea transversal:** dueña del **README** + **coordina los merges** (revisa los Pull Requests y mantiene `main` compilando).
**En la defensa muestra:** dar de alta una categoría y un producto, marcarlo agotado, y verlo reflejado en el menú.

---

### 🟩 Sheilyn — Módulo MESAS + EMPLEADOS
**Rama:** `feature/salon` · **Entidades:** `Mesa`, `Usuario`

| HU | Qué hacer |
|----|-----------|
| **HU-02** | **Gestión de empleados**: crear/editar/desactivar usuarios con un rol (sin autoregistro; sin duplicados). |
| **HU-11** | **Apertura de mesa**: elegir una mesa libre, marcarla ocupada y dejar el pedido en curso listo para Ronald. |
| **HU-15** | **Estado de las mesas**: panel con cada mesa en `LIBRE` / `OCUPADA` / `POR_COBRAR`. |

**En la defensa muestra:** crear un empleado, abrir una mesa y ver cómo cambia el panel de estados.

---

### 🟥 Ronald — Módulo PEDIDOS (el corazón)
**Rama:** `feature/pedidos` · **Entidades:** `Pedido`, `DetallePedido`

| HU | Qué hacer |
|----|-----------|
| **HU-12** | **Agregar ítems** al pedido (producto, cantidad) con el **total recalculado en vivo**. |
| **HU-13** | **Pedido para llevar**: tipo `PARA_LLEVAR`, sin mesa, capturando nombre y teléfono. |
| **HU-14** | **Modificar ítems** antes de enviar a cocina (cambiar cantidad, eliminar, recalcular). |
| **HU-16** | **Enviar pedido a cocina** (pasa a `EN_PREPARACION` y aparece en el tablero de Sofía). |
| **HU-21** | **Notas por ítem** (campo `nota` en cada línea; las de alergia resaltadas). |

> Es el módulo **más pesado**, por eso no lleva tarea transversal.
> Para la **entrega final** (no este avance), Ronald arma la **Seguridad/login** con Spring Security.

**En la defensa muestra:** tomar un pedido en una mesa, agregar ítems con notas, modificar cantidades, y enviarlo a cocina.

---

### 🟨 Sofía — Módulo COCINA + COBRO
**Rama:** `feature/cocina-cobro` · **Entidad:** `Pago`

| HU | Qué hacer |
|----|-----------|
| **HU-17** | **Tablero de cocina** con actualización automática (**polling**): muestra pedidos pendientes, ítems, cantidades y notas/alergias. |
| **HU-18** | **Marcar pedido como listo** (cambia de estado, sale de la cola, registra la hora). |
| **HU-19** | **Cobro + tiquete PDF**: registrar el `Pago` (un método por pedido), marcar `PAGADO`, liberar la mesa y **generar el tiquete en PDF**. |
| **HU-20** | **Reporte de ventas del día** (total vendido + productos más vendidos, desde los pedidos cobrados). |

> 🎯 **Esto cubre la "investigación adicional" que pide la rúbrica:** el **tiquete en PDF** (ej. con OpenPDF/iText) y el **polling** del tablero son tecnologías que no se ven en clase.

**En la defensa muestra:** un pedido llegando al tablero de cocina, marcarlo listo, cobrarlo, y descargar el tiquete PDF.

---

## 4. Cómo conectan los módulos (para que no choquen)

Es un solo flujo real, así que hay 3 puntos de "relevo":

```
Sheilyn (abre mesa)  →  Ronald (toma el pedido, lo envía a cocina)
                                   →  Sofía (cocina lo prepara y marca listo, luego cobra)
```

- **Sheilyn → Ronald:** Sheilyn deja la mesa ocupada con un pedido en curso; Ronald le agrega los ítems.
- **Ronald → Sofía:** Ronald pone el pedido en `EN_PREPARACION`; el tablero de Sofía lo lee.
- **Sofía → Sheilyn:** al cobrar, Sofía libera la mesa y vuelve a `LIBRE`.

Como cada uno trabaja con los **datos seed**, pueden avanzar en paralelo y conectar al final.

---

## 5. GitHub (¡esto vale 30% de la nota!)

- **Una rama por módulo** (`feature/menu`, `feature/salon`, `feature/pedidos`, `feature/cocina-cobro`).
- **Commits seguidos y pequeños** durante toda la semana — NO un solo commit gigante al final. La rúbrica pide aportes "trazables" de **todos**.
- Cada módulo entra a `main` por **Pull Request**, revisado por otra persona (Michelle coordina).
- `main` **siempre debe compilar**. Nadie trabaja directo en `main`.

---

## 6. Cómo se cubre la rúbrica del Avance 2

| Criterio | Peso | Cómo lo cubrimos |
|----------|------|------------------|
| Participación de miembros | 15% | Cada quien construye y **explica su propio módulo** en la defensa. |
| Implementación de HU | 25% | Cubrimos **~16 HU** (todas menos las 3 de login) → muy por encima del 40% pedido. |
| Funcionalidad y fidelidad | 30% | App corriendo con datos reales, **Bootstrap**, fiel al prototipo de Figma. |
| Participación en GitHub | 30% | Rama + Pull Requests + commits de cada integrante. |

**Cobertura de las 21 HU en este avance:**
- ✅ Listas/base: HU-05, HU-10.
- 🔨 A construir: HU-02, 06, 07, 08, 09, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21.
- ⏳ Para la entrega final (necesitan Spring Security): HU-01, HU-03, HU-04.

---

## 7. Recordatorios

- **Seguridad / login (HU-01, 03, 04)** queda para la **entrega final**, cuando el profe enseñe Spring Security (lo arma Ronald). El Avance 2 **no** exige login, así que está bien — pero para la final **sí es obligatorio**, no lo dejen para último momento.
- Todos deben tener la base `soda_candy` creada (correr `creaTablas.sql` una vez) y la app levantando en `http://localhost/`.
- Ante cualquier duda del patrón, **miren cómo está hecho `Rol`** — esa es la referencia.
