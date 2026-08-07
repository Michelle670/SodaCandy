//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.domain.DetallePedido;
import com.sodacandy.domain.EstadoPedido;
import com.sodacandy.domain.LineaCobro;
import com.sodacandy.domain.MetodoPago;
import com.sodacandy.domain.Pago;
import com.sodacandy.domain.Pedido;
import com.sodacandy.domain.PedidoCobro;
import com.sodacandy.domain.Producto;
import com.sodacandy.repository.DetallePedidoRepository;
import com.sodacandy.service.PagoService;
import com.sodacandy.service.PedidoService;
import com.sodacandy.service.ProductoService;
import com.sodacandy.service.TiqueteService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
 * Controlador del modulo COBRO - HU-19.
 *
 * Flujo:
 *   - Lista los pedidos SERVIDO (listos, por cobrar).
 *   - Al elegir uno, muestra su detalle con subtotales y total.
 *   - El cajero digita el MONTO RECIBIDO:
 *       * si es menor al total  -> toast "Saldo insuficiente", NO cobra.
 *       * si alcanza            -> registra el Pago (monto = TOTAL), pasa el
 *                                  pedido a PAGADO, y deja listo el tiquete.
 *   - Si el pedido ya fue cobrado -> toast "Este pedido ya fue cobrado"
 *     (evita el error de generar el tiquete dos veces).
 *
 * Nota: en Pago.monto se guarda el TOTAL del pedido (no lo recibido). Lo
 * recibido solo sirve para validar y calcular el vuelto; guardar lo recibido
 * inflaria el reporte de ventas.
 */
//==============================================================================
@Controller
@RequestMapping("/cobro")
public class CobroController {

    private final PedidoService pedidoService;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoService productoService;
    private final PagoService pagoService;
    private final TiqueteService tiqueteService;

    public CobroController(PedidoService pedidoService,
                           DetallePedidoRepository detallePedidoRepository,
                           ProductoService productoService,
                           PagoService pagoService,
                           TiqueteService tiqueteService) {
        this.pedidoService = pedidoService;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoService = productoService;
        this.pagoService = pagoService;
        this.tiqueteService = tiqueteService;
    }

    //==========================================================
    // GET /cobro/listado - lista de pedidos por cobrar (SERVIDO)
    //==========================================================
    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) Integer idPedido, Model model) {
        List<PedidoCobro> pendientes = armarPendientes();
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("metodos", MetodoPago.values());
        model.addAttribute("idSeleccionado", idPedido);
        model.addAttribute("modulo", "cobros");

        if (idPedido != null) {
            pendientes.stream()
                    .filter(p -> p.getIdPedido().equals(idPedido))
                    .findFirst()
                    .ifPresent(sel -> model.addAttribute("seleccionado", sel));
        }
        return "cobro/listado";
    }

    //==========================================================
    // POST /cobro/cobrar - valida el monto, registra el pago y
    // REDIRIGE con toast. (El PDF se descarga aparte, en /tiquete.)
    //==========================================================
    @PostMapping("/cobrar")
    public String cobrar(@RequestParam Integer idPedido,
                         @RequestParam MetodoPago metodo,
                         @RequestParam BigDecimal montoRecibido,
                         RedirectAttributes ra) {

        // A) Ya fue cobrado -> toast (evita generar el tiquete dos veces)
        if (pagoService.getPagoPorPedido(idPedido).isPresent()) {
            ra.addFlashAttribute("error", "Este pedido ya fue cobrado.");
            return "redirect:/cobro/listado";
        }

        PedidoCobro pc = pedidoService.getPedido(idPedido).map(this::construir).orElse(null);
        if (pc == null) {
            ra.addFlashAttribute("error", "No existe el pedido.");
            return "redirect:/cobro/listado";
        }

        // B) Validar el monto recibido contra el total
        if (montoRecibido == null || montoRecibido.compareTo(pc.getTotal()) < 0) {
            ra.addFlashAttribute("error", "Saldo insuficiente: el monto no cubre el total.");
            // vuelve al mismo pedido seleccionado para reintentar
            return "redirect:/cobro/listado?idPedido=" + idPedido;
        }

        // C) Registrar el Pago (monto = TOTAL, no lo recibido)
        Pago pago = new Pago();
        pago.setIdPedido(idPedido);
        pago.setMonto(pc.getTotal());
        pago.setMetodo(metodo);
        pago.setFecha(LocalDateTime.now());
        pagoService.save(pago);

        // D) El pedido pasa a PAGADO
        pedidoService.getPedido(idPedido).ifPresent(ped -> {
            ped.setEstado(EstadoPedido.PAGADO);
            pedidoService.save(ped);
        });

        // E) TODO Sheilyn: liberar la mesa cuando exista MesaService
        //    mesaService.liberar(pc.getIdMesa());

        // F) Calcular vuelto y avisar con toast de exito + link al tiquete
        BigDecimal vuelto = montoRecibido.subtract(pc.getTotal());
        ra.addFlashAttribute("todoOk",
            "Cobro realizado. Vuelto: \u20A1" + vuelto);
        ra.addFlashAttribute("tiquetePedido", idPedido); // la vista usa esto para descargar

        return "redirect:/cobro/listado";
    }

    //==========================================================
    // GET /cobro/tiquete/{idPedido} - genera y descarga el PDF
    // (se llama despues de cobrar; el pedido ya tiene su Pago)
    //==========================================================
    @GetMapping("/tiquete/{idPedido}")
    public ResponseEntity<byte[]> tiquete(@PathVariable Integer idPedido) {
        PedidoCobro pc = pedidoService.getPedido(idPedido).map(this::construir).orElse(null);
        if (pc == null) {
            return ResponseEntity.notFound().build();
        }
        // el metodo del pago ya guardado
        String metodo = pagoService.getPagoPorPedido(idPedido)
                .map(p -> p.getMetodo().name())
                .orElse("");

        byte[] pdf = tiqueteService.generarTiquete(pc, metodo);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "tiquete-pedido-" + idPedido + ".pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    //==========================================================
    // Helpers
    //==========================================================
    private List<PedidoCobro> armarPendientes() {
        List<PedidoCobro> res = new ArrayList<>();
        for (Pedido pedido : pedidoService.getPedidos()) {
            if (pedido.getEstado() == EstadoPedido.SERVIDO) {
                res.add(construir(pedido));
            }
        }
        return res;
    }

    private PedidoCobro construir(Pedido pedido) {
        List<LineaCobro> lineas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (DetallePedido det : detallePedidoRepository.findByIdPedido(pedido.getIdPedido())) {
            String nombre = productoService.getProducto(det.getIdProducto())
                    .map(Producto::getDescripcion)
                    .orElse("Producto #" + det.getIdProducto());

            BigDecimal precio = (det.getPrecioHistorico() != null)
                    ? det.getPrecioHistorico() : BigDecimal.ZERO;
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(det.getCantidad()));

            lineas.add(new LineaCobro(det.getCantidad(), nombre, precio, subtotal));
            total = total.add(subtotal);
        }
        return new PedidoCobro(pedido.getIdPedido(), pedido.getIdMesa(), lineas, total);
    }
}