//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;

import com.sodacandy.domain.Pedido;
import com.sodacandy.service.PedidoService;
import com.sodacandy.service.ProductoService;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

@Controller// Anotacion controller
@RequestMapping("/pedido")// todo lo que se pida bajo /rol lo resuelve este codigo
public class PedidoController {

    //Para enlazar el servicio del rol y los mensajes i18n
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final MessageSource messageSource;

    public PedidoController(ProductoService productoService, PedidoService pedidoService, MessageSource messageSource) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.messageSource = messageSource;
    }

    //metodo que inserta o actualiza (segun venga o no el idPedido)
    @GetMapping("/nuevo")
    public String nuevoPedido(@RequestParam(value = "idMesa", required = false) Integer idMesa, Model model) {
        Pedido pedido = new Pedido();

        // Valores comunes obligatorios para CUALQUIER tipo de pedido nuevo
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
        
        // 3. Se cargan los productos
        model.addAttribute("listaProductos", productoService.getProductos(true));

        return "/pedido/formulario";
    }

    // Método para generar el pedido con todas las indicaciones y enviarlo a cocina
    @PostMapping("/generar")
    public String generarPedido(Pedido pedido, RedirectAttributes redirectAttributes) {
        
        // Aseguramos la coherencia del timestamp justo antes de insertar
        pedido.setFecha(java.time.LocalDateTime.now());
        
        // Se genera el ID del pedido
        pedidoService.save(pedido);
        
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

}
