//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.domain.LineaReporte;
import com.sodacandy.service.PagoService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
@Controller
@RequestMapping("/reporte")
public class ReporteController {

    private final PagoService pagoService;

    public ReporteController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping("/ventas")
    public String ventas(Model model) {
        List<LineaReporte> lineas = pagoService.getReporteVentas();

        // Totales generales (para la fila final)
        int totalCantidad = lineas.stream().mapToInt(LineaReporte::getCantidad).sum();
        BigDecimal totalVendido = lineas.stream()
                .map(LineaReporte::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("lineas", lineas);
        model.addAttribute("totalCantidad", totalCantidad);
        model.addAttribute("totalVendido", totalVendido);
        model.addAttribute("modulo", "reportes");
        return "reporte/ventas";
    }
}