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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
/**
 * Controlador del modulo COBRO  - HU-19.
 *
 * Flujo:
 *   - Lista los pedidos SERVIDO (listos, por cobrar).
 *   - Al elegir uno, muestra su detalle con subtotales y total.
 *   - Al cobrar: registra el Pago, pasa el pedido a PAGADO, y genera el
 *     tiquete PDF descargable (TiqueteService / OpenPDF).
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
    // Si viene ?idPedido=, ademas carga ese detalle a la derecha.
    //==========================================================
    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) Integer idPedido, Model model) {
        List<PedidoCobro> pendientes = armarPendientes();
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("metodos", MetodoPago.values());
        model.addAttribute("idSeleccionado", idPedido);

        if (idPedido != null) {
            pendientes.stream()
                    .filter(p -> p.getIdPedido().equals(idPedido))
                    .findFirst()
                    .ifPresent(sel -> model.addAttribute("seleccionado", sel));
        }
        return "cobro/listado";
    }

    //==========================================================
    // POST /cobro/cobrar - registra el pago y devuelve el tiquete PDF
    //==========================================================
    @PostMapping("/cobrar")
    public ResponseEntity<byte[]> cobrar(@RequestParam Integer idPedido,
                                         @RequestParam MetodoPago metodo) {

        // Evita cobrar dos veces el mismo pedido (un solo Pago por pedido)
        if (pagoService.getPagoPorPedido(idPedido).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        PedidoCobro pc = pedidoService.getPedido(idPedido).map(this::construir).orElse(null);
        if (pc == null) {
            return ResponseEntity.notFound().build();
        }

        // 1) Registrar el Pago (monto = total del pedido)
        Pago pago = new Pago();
        pago.setIdPedido(idPedido);
        pago.setMonto(pc.getTotal());
        pago.setMetodo(metodo);
        pago.setFecha(LocalDateTime.now());
        pagoService.save(pago);

        // 2) El pedido pasa a PAGADO (sale de la lista de por cobrar)
        pedidoService.getPedido(idPedido).ifPresent(ped -> {
            ped.setEstado(EstadoPedido.PAGADO);
            pedidoService.save(ped);
        });

        // 3) TODO Sheilyn: liberar la mesa cuando exista MesaService
        //    mesaService.liberar(pc.getIdMesa());

        // 4) Generar el tiquete PDF y devolverlo para descarga
        byte[] pdf = tiqueteService.generarTiquete(pc, metodo.name());
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