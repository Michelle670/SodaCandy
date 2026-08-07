//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;

import com.sodacandy.domain.LineaReporte;
import com.sodacandy.service.PagoService;
import com.sodacandy.service.ReportePdfService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
/**
 * Reporte de ventas del DIA (HU-20).
 * Cuenta solo los pagos de hoy (Opcion A: "vuelve a cero" cada dia, sin borrar).
 * Muestra tarjetas (ventas del dia / pedidos cobrados) + desglose por metodo,
 * y permite generar el PDF del dia (OpenPDF).
 */
@Controller
@RequestMapping("/reporte")
public class ReporteController {

    private final PagoService pagoService;
    private final ReportePdfService reportePdfService;

    public ReporteController(PagoService pagoService, ReportePdfService reportePdfService) {
        this.pagoService = pagoService;
        this.reportePdfService = reportePdfService;
    }

    //==========================================================
    // GET /reporte/ventas - pantalla del reporte del dia
    //==========================================================
    @GetMapping("/ventas")
    public String ventas(Model model) {
        List<LineaReporte> lineas = pagoService.getReporteVentasDelDia();

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

    //==========================================================
    // GET /reporte/pdf - genera y descarga el PDF del dia
    // (No borra nada: es solo una "foto" de las ventas de hoy.)
    //==========================================================
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf() {
        List<LineaReporte> lineas = pagoService.getReporteVentasDelDia();

        int totalCantidad = lineas.stream().mapToInt(LineaReporte::getCantidad).sum();
        BigDecimal totalVendido = lineas.stream()
                .map(LineaReporte::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        byte[] pdf = reportePdfService.generarReporteDia(lineas, totalCantidad, totalVendido);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-ventas-dia.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}