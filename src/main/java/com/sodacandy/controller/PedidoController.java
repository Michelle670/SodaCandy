//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;

import com.sodacandy.domain.LineaCocina;
import com.sodacandy.domain.Pedido;
import com.sodacandy.domain.PedidoCocina;
import com.sodacandy.domain.Producto;
import com.sodacandy.service.CategoriaService;
import com.sodacandy.service.PedidoService;
import com.sodacandy.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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

    public PedidoController(CategoriaService categoriaService, ProductoService productoService, PedidoService pedidoService, MessageSource messageSource) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.messageSource = messageSource;
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
            // El nombreCliente y telefono se mapean desde el modal
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
        if (session.getAttribute("itemsPedido") == null) {
            session.setAttribute("itemsPedido", new ArrayList<LineaCocina>());
        }
        
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
        if (items == null) items = new ArrayList<>();
        
        // 2. Se busca el producto en la base de datos para obtener los datos
        Producto productoEncontrado = productoService.getProducto(idProducto).orElse(null);
        
        if (productoEncontrado != null) {
            String nombre = productoEncontrado.getDescripcion();
            boolean existe = false;
            
            // 3. Comparar si ya existe el platillo en la lista por su nombre
            for (LineaCocina item : items) {
                if (item.getNombreProducto() != null && item.getNombreProducto().equalsIgnoreCase(nombre)) {
                    item.setCantidad(item.getCantidad() + 1);
                    existe = true;
                    break;
                }
            }

            // 4. Si es nuevo, se instancia usando el constructor LineaCocina(cantidad, nombreProducto, nota)
            if (!existe) {
                LineaCocina nuevoItem = new LineaCocina(1, nombre, "");
                items.add(nuevoItem);
            }
        }
        // 7. Se resscribe el estado de la sesion
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
    
    // =========================================================================
    // MÉTODOS PARA GESTIONAR LOS PEDIDOS
    // =========================================================================
    
    @PostMapping("/generar")
    public String generarPedido(Pedido pedido, HttpSession session, RedirectAttributes redirectAttributes) {

        // Aseguramos la coherencia del timestamp justo antes de insertar
        pedido.setFecha(java.time.LocalDateTime.now());
        
        // Recuperar productos guardados en la sesión
        @SuppressWarnings("unchecked")
        List<LineaCocina> productos = (List<LineaCocina>) session.getAttribute("itemsPedido");
        if (productos == null) {
            productos = new ArrayList<>();
        }
        
        // Se guarda el pedido completo
        pedidoService.save(pedido);

        // Se genera el pedido que será enviado a cocina
        PedidoCocina pedidoCocina = new PedidoCocina(pedido.getIdPedido(), pedido.getIdMesa(), productos);
        
        // Limpiar la sesión tras completar el pedido
        session.removeAttribute("itemsPedido");
        
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));

        return "redirect:/cocina/tablero";
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
    public String consulta(@RequestParam("idCategoria") Integer idCategoria, @RequestParam(value = "idMesa", required = false) Integer idMesa, Model model) {

        Pedido pedido = new Pedido();
        pedido.setIdMesa(idMesa);
        model.addAttribute("pedido", pedido);
        model.addAttribute("idMesa", idMesa);

        var productos = productoService.consulta(idCategoria);
        model.addAttribute("listaProductos", productos);
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        return "/pedido/formulario";
    }

}
