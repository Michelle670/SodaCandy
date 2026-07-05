//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.domain.DetallePedido;
import com.sodacandy.domain.EstadoPedido;
import com.sodacandy.domain.LineaCocina;
import com.sodacandy.domain.Pedido;
import com.sodacandy.domain.PedidoCocina;
import com.sodacandy.repository.DetallePedidoRepository;
import com.sodacandy.service.PedidoService;
import com.sodacandy.service.ProductoService;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
/**
 * Controlador del modulo COCINA.
 * Usa los servicios REALES de (Pedido) y de (Producto).
 *
 *   HU-17: Tablero en tiempo real -> pedidos EN_PREPARACION con items y notas.
 *   HU-18: Marcar listo -> pasa el pedido a SERVIDO.
 *
 * En el modelo el detalle vive aparte del pedido y solo guarda
 * idProducto, asi que este controlador arma un PedidoCocina por cada pedido:
 * junta el pedido + sus detalles (DetallePedidoRepository) + el nombre del
 * producto (ProductoService).
 */
//==============================================================================
@Controller
@RequestMapping("/cocina")
public class CocinaController {

    private final PedidoService pedidoService;                    
    private final DetallePedidoRepository detallePedidoRepository; 
    private final ProductoService productoService;                
    private final MessageSource messageSource;

    public CocinaController(PedidoService pedidoService,
                            DetallePedidoRepository detallePedidoRepository,
                            ProductoService productoService,
                            MessageSource messageSource) {
        this.pedidoService = pedidoService;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoService = productoService;
        this.messageSource = messageSource;
    }

    //==========================================================
    // GET /cocina/tablero - HU-17: cola de pedidos EN_PREPARACION
    //==========================================================
    @GetMapping("/tablero")
    public String tablero(Model model) {
        List<PedidoCocina> pedidos = armarTarjetas(EstadoPedido.EN_PREPARACION);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalPedidos", pedidos.size());
        model.addAttribute("modulo", "cocina");   // resalta "Cocina" en el sidebar
        return "cocina/tablero";
    }

    //==========================================================
    // GET /cocina/historial - pedidos ya LISTO
    //==========================================================
    @GetMapping("/historial")
    public String historial(Model model) {
        List<PedidoCocina> pedidos = armarTarjetas(EstadoPedido.SERVIDO);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalPedidos", pedidos.size());
        model.addAttribute("modulo", "cocina");
        return "cocina/historial";
    }

    //==========================================================
    // POST /cocina/listo - HU-18: marcar pedido como listo
    // Ronald no tiene "cambiarEstado", asi que usamos getPedido + save.
    //==========================================================
    @PostMapping("/listo")
    public String marcarListo(@RequestParam Integer idPedido, RedirectAttributes redirectAttributes) {
        pedidoService.getPedido(idPedido).ifPresentOrElse(
            pedido -> {
                pedido.setEstado(EstadoPedido.SERVIDO);
                pedidoService.save(pedido);
                redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("cocina.listo.ok", null, Locale.getDefault()));
            },
            () -> redirectAttributes.addFlashAttribute("error",
                    "No existe el pedido id " + idPedido)
        );
        return "redirect:/cocina/tablero";
    }

    //==========================================================
    // Helper: arma las tarjetas (pedido + detalles + nombre producto)
    //==========================================================
    private List<PedidoCocina> armarTarjetas(EstadoPedido estado) {
        List<PedidoCocina> resultado = new ArrayList<>();

        // 1) Recorre los pedidos y se queda con los del estado buscado
        for (Pedido pedido : pedidoService.getPedidos()) {
            if (pedido.getEstado() != estado) {
                continue;
            }
            // 2) Trae las lineas de ese pedido
            List<LineaCocina> lineas = new ArrayList<>();
            for (DetallePedido det : detallePedidoRepository.findByIdPedido(pedido.getIdPedido())) {
                // 3) Nombre del producto = "descripcion" en la entidad de Michelle
                //    (con respaldo si el producto no se encuentra)
                String nombre = productoService.getProducto(det.getIdProducto())
                        .map(p -> p.getDescripcion())
                        .orElse("Producto #" + det.getIdProducto());
                lineas.add(new LineaCocina(det.getCantidad(), nombre, det.getNota()));
            }
            resultado.add(new PedidoCocina(pedido.getIdPedido(), pedido.getIdMesa(), lineas));
        }
        return resultado;
    }
}