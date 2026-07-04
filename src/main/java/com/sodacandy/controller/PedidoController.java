//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.domain.Pedido;
import com.sodacandy.service.PedidoService;
import jakarta.validation.Valid;
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
@Controller// Anotacion controller
@RequestMapping("/pedido")// todo lo que se pida bajo /rol lo resuelve este codigo
public class PedidoController
{
    //Para enlazar el servicio del rol y los mensajes i18n
    private final PedidoService pedidoService;
    private final MessageSource messageSource;

    public PedidoController(PedidoService pedidoService, MessageSource messageSource) {
        this.pedidoService = pedidoService;
        this.messageSource = messageSource;
    }


    //metodo del listado
    @GetMapping("/listado")
    public String listado(Model model)
    {
        var pedidos = pedidoService.getPedidos();
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalPedidos", pedidos.size());
        return "/pedido/listado";
    }

    //metodo que abre la pagina de edicion
    @GetMapping("/modificar/{idPedido}")
    public String modificar(@PathVariable("idPedido") Integer idPedido,
            Model model,
            RedirectAttributes redirectAttributes)
    {
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
    public String guardar(@Valid Pedido pedido, RedirectAttributes redirectAttributes)
    {
        pedidoService.save(pedido);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/pedido/listado";
    }

    //metodo que elimina
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idPedido, RedirectAttributes redirectAttributes)
    {
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
