package com.sodacandy.controller;

import com.sodacandy.domain.Pedido;
import com.sodacandy.domain.EstadoPedido;
import com.sodacandy.domain.TipoPedido;
import com.sodacandy.service.PedidoService;
import jakarta.validation.Valid;
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

@Controller// Anotacion controller
@RequestMapping("/pedido")// todo lo que se pida bajo /rol lo resuelve este codigo
public class PedidoController {

    //Para enlazar el servicio del rol y los mensajes i18n
    private final PedidoService pedidoService;
    private final MessageSource messageSource;

    public PedidoController(PedidoService pedidoService, MessageSource messageSource) {
        this.pedidoService = pedidoService;
        this.messageSource = messageSource;
    }

    //metodo del listado
    @GetMapping("/listado")
    public String listado(Model model) {
        List<Pedido> pedidos = pedidoService.getPedidos();
        model.addAttribute("pedidos", pedidos);

        // Contadores basados en el flujo filtrado
        long enCurso = pedidos.stream().filter(p -> "EN_PREPARACION".equals(p.getEstado().name()) || "PENDIENTE".equals(p.getEstado().name()) || "SERVIDO".equals(p.getEstado().name())).count();
        long pagados = pedidos.stream().filter(p -> "PAGADO".equals(p.getEstado().name()) || "ANULADO".equals(p.getEstado().name())).count();

        model.addAttribute("totalPedidosEnCurso", enCurso);
        model.addAttribute("totalPedidosPagados", pagados);
        model.addAttribute("pedido", new Pedido());

        return "/pedido/listado";
    }

    //metodo que abre la pagina de edicion
    @GetMapping("/modificar/{idPedido}")
    public String modificar(@PathVariable("idPedido") Integer idPedido,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Pedido> pedidoOpt = pedidoService.getPedido(idPedido);
        if (pedidoOpt.isEmpty()) { // si no lo encontraron...
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("rol.error01", null, Locale.getDefault()));
            return "redirect:/pedido/listado";
        }
        model.addAttribute("pedido", pedidoOpt.get());
        return "/pedido/modifica";
    }

    //metodo que inserta o actualiza (segun venga o no el idRol)
    @PostMapping("/guardar")
    public String guardar(Pedido pedido, RedirectAttributes redirectAttributes) {

        // 1. Se verifica si es un INSERT (pedido nuevo) o un UPDATE
        if (pedido.getIdPedido() == null) {

            // Valores comunes obligatorios para CUALQUIER tipo de pedido nuevo
            pedido.setIdUsuario(1); // Temporal: Usuario por defecto (Salonero/Cajero)
            pedido.setTotal(java.math.BigDecimal.ZERO); // Inicializa en 0.00
            pedido.setFecha(java.time.LocalDateTime.now()); // Timestamp actual del sistema

            // Condicional segun el tipo de pedido
            if (pedido.getIdMesa() != null) { // Si el pedido es para una mesa, se definen los siguientes parámetros
                // --- FLUJO A: PEDIDO DE MESA ---
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

        } else {
            // Es un UPDATE: Mantener consistencia si hace falta
            if (pedido.getFecha() == null) {
                pedido.setFecha(java.time.LocalDateTime.now());
            }
        }

        // 2. Persistencia en la base de datos
        pedidoService.save(pedido);

        // 3. Mensaje de éxito
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));

        if (pedido.getTipo() == com.sodacandy.domain.TipoPedido.PARA_LLEVAR) {
            // Si el pedido es para llevar, va directo a la pantalla para seleccionar productos
            return "redirect:/pedido/modificar/" + pedido.getIdPedido();
        } else {
            // Si el pedido es de una mesa, se redirige a la lista de pedidos hasta que el salonero decida tomar la orden de la mesa
            return "redirect:/pedido/listado";
        }
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
