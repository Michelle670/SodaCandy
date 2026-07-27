//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;

import com.sodacandy.domain.DetallePedido;
import com.sodacandy.domain.LineaCocina;
import com.sodacandy.domain.Pedido;
import com.sodacandy.domain.PedidoCocina;
import com.sodacandy.domain.Producto;
import com.sodacandy.service.CategoriaService;
import com.sodacandy.service.DetallePedidoService;
import com.sodacandy.service.PedidoService;
import com.sodacandy.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================

/**
 * Controlador del modulo PEDIDOS (HU-12, HU-13, HU-14, HU-16, HU-21)
 *
 * Flujo: - Muestra los productos disponibles para agregar a la lista del
 * pedido. - Al elegir uno, muestra el item agregado, su cantidad y costo. -
 * Opcion de colocar una nota con indicaciones especiales al item. - El
 * funcionamiento de agregar productos al pedido es igual que el de un Carrito
 * de compras. - Un vez listo el pedido, se genera el objeto PedidoCocina, y se
 * envia a cocina.
 */
//==============================================================================
@Controller// Anotacion controller
@RequestMapping("/pedido")// todo lo que se pida bajo /rol lo resuelve este codigo
public class PedidoController {

    //Para enlazar el servicio del rol y los mensajes i18n
    private final CategoriaService categoriaService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final MessageSource messageSource;
    private final DetallePedidoService detallePedidoService;

    public PedidoController(CategoriaService categoriaService, ProductoService productoService, PedidoService pedidoService, MessageSource messageSource, DetallePedidoService detallePedidoService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.messageSource = messageSource;
        this.detallePedidoService = detallePedidoService;
    }

    //metodo que inserta o actualiza (segun venga o no el idPedido)
    @GetMapping("/nuevo")
    public String nuevoPedido(@RequestParam(value = "idMesa", required = false) Integer idMesa,
            @RequestParam(value = "idCategoria", required = false) Integer idCategoria,
            Model model,
            HttpSession session) {

        // Valores comunes obligatorios para CUALQUIER tipo de pedido nuevo
        Pedido pedido = new Pedido();
        pedido.setIdUsuario(1); // Temporal: Usuario por defecto (Salonero/Cajero)
        pedido.setTotal(java.math.BigDecimal.ZERO); // Inicializa en 0.00
        pedido.setFecha(java.time.LocalDateTime.now()); // Timestamp actual del sistema

        // Condicional segun el tipo de pedido
        if (idMesa != null) { // Si el pedido es para una mesa, se definen los siguientes parámetros
            // --- FLUJO A: PEDIDO DE MESA ---
            pedido.setIdMesa(idMesa);
            pedido.setTipo(com.sodacandy.domain.TipoPedido.MESA);
            pedido.setNombreCliente(null); // No lleva cliente directo, está asociado a la mesa
            pedido.setTelefono(null);
            pedido.setEstado(com.sodacandy.domain.EstadoPedido.PENDIENTE); // Estado inicial de ordenamiento
        } else {
            // --- FLUJO B: PEDIDO PARA LLEVAR ---
            pedido.setTipo(com.sodacandy.domain.TipoPedido.PARA_LLEVAR);
            pedido.setEstado(com.sodacandy.domain.EstadoPedido.EN_PREPARACION); // Estado inicial de ordenamiento
            if (session.getAttribute("nombreClienteParaLlevar") != null) {
                pedido.setNombreCliente((String) session.getAttribute("nombreClienteParaLlevar"));
                pedido.setTelefono((String) session.getAttribute("telefonoParaLlevar"));
            }
        }

        // 2. Se pasa el objeto "vacío" a la vista
        model.addAttribute("pedido", pedido);
        model.addAttribute("idMesa", idMesa);

        // 3. Se cargan los productos y categorías de productos activos para verlos en la vista
        int catActual = (idCategoria != null) ? idCategoria : 0;
        model.addAttribute("categoriaSeleccionada", catActual);

        if (catActual == 0) {
            model.addAttribute("listaProductos", productoService.getProductos(true));
        } else {
            model.addAttribute("listaProductos", productoService.consulta(catActual));
        }
        model.addAttribute("categorias", categoriaService.getCategorias(true));

        // 4. Inicializar la lista de la sesión si aún no existe
        @SuppressWarnings("unchecked")
        List<LineaCocina> items = (List<LineaCocina>) session.getAttribute("itemsPedido");
        if (items == null) {
            items = new ArrayList<>();
            session.setAttribute("itemsPedido", items);
        }
        
        // Calculo del total del pedido
        java.math.BigDecimal totalPedido = java.math.BigDecimal.ZERO;
        for (LineaCocina linea : items) {
            Producto p = productoService.getProducto(linea.getIdProducto()).orElse(null);
            if (p != null && p.getPrecio() != null) {
                java.math.BigDecimal subtotal = p.getPrecio().multiply(new java.math.BigDecimal(linea.getCantidad()));
                totalPedido = totalPedido.add(subtotal);
            }
        }

        // Pasar el total calculado a la vista
        model.addAttribute("totalPedido", totalPedido);
        model.addAttribute("itemsPedido", items);
        // ---------------------------------------------------
        
        return "/pedido/formulario";
    }

    // =========================================================================
    // MÉTODOS DE SESIÓN (LISTA DE PRODUCTOS DEL PEDIDO)
    // =========================================================================
    // Agregar un producto a la lista temporal de la sesión
    @GetMapping("/agregar/{idProducto}")
    public String agregarItem(@PathVariable("idProducto") Integer idProducto,
            @RequestParam(value = "idMesa", required = false) Integer idMesa,
            HttpSession session) {

        // 1. Traemos la lista incializada se la sesión
        @SuppressWarnings("unchecked")
        List<LineaCocina> items = (List<LineaCocina>) session.getAttribute("itemsPedido");
        if (items == null) {
            items = new ArrayList<>();
        }

        // 2. Se busca el producto en la base de datos para obtener los datos
        Producto productoEncontrado = productoService.getProducto(idProducto).orElse(null);

        if (productoEncontrado != null) {
            boolean existe = false;

            // 3. Comparar si ya existe el platillo en la lista
            for (LineaCocina item : items) {
                if (item.getIdProducto() == productoEncontrado.getIdProducto()) {
                    item.setCantidad(item.getCantidad() + 1);
                    existe = true;
                    break;
                }
            }

            // 4. Si es nuevo, se instancia usando el constructor LineaCocina(cantidad, idProducto, nombreProducto, nota)
            if (!existe) {
                LineaCocina nuevoItem = new LineaCocina(1, productoEncontrado.getIdProducto(), productoEncontrado.getDescripcion(), "");
                items.add(nuevoItem);
            }
        }
        // 5. Se resscribe el estado de la sesion
        session.setAttribute("itemsPedido", items);

        // Redirige al formulario manteniendo el ID de mesa si aplica
        if (idMesa != null) {
            return "redirect:/pedido/nuevo?idMesa=" + idMesa;
        }
        return "redirect:/pedido/nuevo";
    }

    @GetMapping("/limpiar")
    public String limpiarPedido(@RequestParam(value = "idMesa", required = false) Integer idMesa, HttpSession session) {
        session.removeAttribute("itemsPedido");
        if (idMesa != null) {
            return "redirect:/pedido/nuevo?idMesa=" + idMesa;
        }
        return "redirect:/pedido/nuevo";
    }

    // Eliminar un ítem específico de la sesión por su posición en la lista
    @GetMapping("/eliminarItem/{index}")
    public String eliminarItem(@PathVariable("index") int index,
            @RequestParam(value = "idMesa", required = false) Integer idMesa,
            HttpSession session) {

        @SuppressWarnings("unchecked")
        List<LineaCocina> items = (List<LineaCocina>) session.getAttribute("itemsPedido");

        if (items != null && index >= 0 && index < items.size()) {
            items.remove(index); // Remueve el elemento de la lista temporal
            session.setAttribute("itemsPedido", items);
        }

        if (idMesa != null) {
            return "redirect:/pedido/nuevo?idMesa=" + idMesa;
        }
        return "redirect:/pedido/nuevo";
    }

    // Actualizar la nota de un ítem específico en la sesión
    @PostMapping("/actualizarNota/{index}")
    public String actualizarNota(@PathVariable("index") int index,
            @RequestParam("nota") String nota,
            @RequestParam(value = "idMesa", required = false) Integer idMesa,
            HttpSession session) {

        @SuppressWarnings("unchecked")
        List<LineaCocina> items = (List<LineaCocina>) session.getAttribute("itemsPedido");

        if (items != null && index >= 0 && index < items.size()) {
            items.get(index).setNota(nota); // Actualiza la nota del producto en esa posición
            session.setAttribute("itemsPedido", items);
        }

        if (idMesa != null) {
            return "redirect:/pedido/nuevo?idMesa=" + idMesa;
        }
        return "redirect:/pedido/nuevo";
    }

    // =========================================================================
    // MÉTODOS PARA GESTIONAR LOS PEDIDOS
    // =========================================================================
    @PostMapping("/generar")
    public String generarPedido(Pedido pedido, HttpSession session, RedirectAttributes redirectAttributes) {

        // 1. Recuperar productos guardados en la sesión
        @SuppressWarnings("unchecked")
        List<LineaCocina> productos = (List<LineaCocina>) session.getAttribute("itemsPedido");
        if (productos == null) {
            productos = new ArrayList<>();
        }

        // Validación adicional por si intentan enviar el pedido vacío
        if (productos.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se puede generar un pedido sin productos.");
            return "redirect:/pedido/nuevo" + (pedido.getIdMesa() != null ? "?idMesa=" + pedido.getIdMesa() : "");
        }

        // 2. Configurar datos obligatorios iniciales del pedido para evitar nulos en BD    
        pedido.setFecha(java.time.LocalDateTime.now());

        if (pedido.getIdUsuario() == null) {
            pedido.setIdUsuario(1);
        }
        if (pedido.getEstado() == null) {
            pedido.setEstado(com.sodacandy.domain.EstadoPedido.PENDIENTE);
        }
        if (pedido.getTipo() == null) {
            if (pedido.getIdMesa() != null) {
                pedido.setTipo(com.sodacandy.domain.TipoPedido.MESA);
            } else {
                pedido.setTipo(com.sodacandy.domain.TipoPedido.PARA_LLEVAR);
                pedido.setNombreCliente((String) session.getAttribute("nombreClienteParaLlevar"));
                pedido.setTelefono((String) session.getAttribute("telefonoParaLlevar"));
            }
        }
        if (pedido.getTotal() == null) {
            pedido.setTotal(java.math.BigDecimal.ZERO);
        }

        // 3. Calcular el total del pedido sumando las líneas del pedido
        java.math.BigDecimal totalCalculado = java.math.BigDecimal.ZERO;
        List<DetallePedido> detallesParaGuardar = new ArrayList<>(); // Lista temporal para almacenar los detalles listos para guardar

        for (LineaCocina linea : productos) {
            DetallePedido detalle = new DetallePedido();

            detalle.setIdPedido(pedido.getIdPedido());
            detalle.setIdProducto(linea.getIdProducto());
            detalle.setCantidad(linea.getCantidad());

            // Buscar el producto real para extraer su precio actual (precio histórico)
            Producto productoReal = productoService.getProducto(linea.getIdProducto()).orElse(null);
            if (productoReal != null && productoReal.getPrecio() != null) {
                detalle.setPrecioHistorico(productoReal.getPrecio());

                // Calcular subtotal de la línea y sumarlo al total general del pedido
                java.math.BigDecimal subtotal = productoReal.getPrecio().multiply(new java.math.BigDecimal(linea.getCantidad()));
                totalCalculado = totalCalculado.add(subtotal);
            } else {
                detalle.setPrecioHistorico(java.math.BigDecimal.ZERO);
            }

            detalle.setNota(linea.getNota());
            detallesParaGuardar.add(detalle);
        }
        // Asignar el total calculado al pedido principal
        pedido.setTotal(totalCalculado);

        // 4. Guardar pedido principal en la base de datos
        pedidoService.save(pedido);

        // 5. Asignar el idPedido generado a cada detalle y guardarlos
        for (DetallePedido detalle : detallesParaGuardar) {
            detalle.setIdPedido(pedido.getIdPedido());
            detallePedidoService.save(detalle);
        }

        // Crear objeto PedidoCocina con la lista acumulada
        PedidoCocina pedidoCocina = new PedidoCocina(pedido.getIdPedido(), pedido.getIdMesa(), productos);

        // Limpiar la sesión tras completar el pedido
        session.removeAttribute("itemsPedido");
        session.removeAttribute("nombreClienteParaLlevar");
        session.removeAttribute("telefonoParaLlevar");

        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));

        return "redirect:/cocina/tablero";
    }

    // Método para el flujo de pedidos para llevar
    @GetMapping("/iniciarParaLlevar")
    public String iniciarParaLlevar(@RequestParam("nombreCliente") String nombreCliente,
            @RequestParam("telefono") String telefono,
            HttpSession session) {

        // Guardamos temporalmente los datos del cliente en la sesión
        session.setAttribute("nombreClienteParaLlevar", nombreCliente);
        session.setAttribute("telefonoParaLlevar", telefono);

        // Redirige al formulario principal de pedidos sin mesa
        return "redirect:/pedido/nuevo";
    }

    //metodo que elimina
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idPedido, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            pedidoService.delete(idPedido);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // el rol no existe
            detalle = "rol.error01";
        } catch (IllegalStateException e) {
            titulo = "error"; // el rol tiene usuarios asociados
            detalle = "rol.error02";
        } catch (Exception e) {
            titulo = "error"; // cualquier otro error inesperado
            detalle = "rol.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/pedido/listado";
    }

    @PostMapping("/consulta")
    public String consulta(@RequestParam("idCategoria") Integer idCategoria, 
                           @RequestParam(value = "idMesa", required = false) Integer idMesa, 
                           Model model, 
                           HttpSession session) {

        Pedido pedido = new Pedido();
        pedido.setIdMesa(idMesa);
        
        // Mantener tipo y datos si es para llevar
        if (idMesa == null && session.getAttribute("nombreClienteParaLlevar") != null) {
            pedido.setTipo(com.sodacandy.domain.TipoPedido.PARA_LLEVAR);
            pedido.setNombreCliente((String) session.getAttribute("nombreClienteParaLlevar"));
            pedido.setTelefono((String) session.getAttribute("telefonoParaLlevar"));
        }
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("idMesa", idMesa);

        // Cargar ítems y calcular total actual en sesión para que no se pierda al filtrar
        @SuppressWarnings("unchecked")
        List<LineaCocina> items = (List<LineaCocina>) session.getAttribute("itemsPedido");
        if (items == null) {
            items = new ArrayList<>();
        }
        
        java.math.BigDecimal totalPedido = java.math.BigDecimal.ZERO;
        for (LineaCocina linea : items) {
            Producto p = productoService.getProducto(linea.getIdProducto()).orElse(null);
            if (p != null && p.getPrecio() != null) {
                totalPedido = totalPedido.add(p.getPrecio().multiply(new java.math.BigDecimal(linea.getCantidad())));
            }
        }

        model.addAttribute("totalPedido", totalPedido);
        model.addAttribute("itemsPedido", items);

        // Filtrar productos por categoría seleccionada
        var productos = (idCategoria == 0) ? productoService.getProductos(true) : productoService.consulta(idCategoria);
        model.addAttribute("listaProductos", productos);
        model.addAttribute("categoriaSeleccionada", idCategoria);
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        
        return "/pedido/formulario";
    }

}
