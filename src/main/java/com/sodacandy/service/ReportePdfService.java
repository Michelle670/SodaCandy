package com.sodacandy.service;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sodacandy.domain.LineaReporte;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

/*
 * ReportePdfService — genera el PDF del reporte de ventas del dia (HU-20).
 * Reutiliza OpenPDF (misma libreria del tiquete de cobro). Ver la leyenda de
 * TiqueteService para el detalle de por que se eligio OpenPDF.
 */
@Service
public class ReportePdfService {

    private static final String SIMBOLO = "\u20A1"; // colon
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * @param lineas        totales por metodo de pago (Efectivo/Tarjeta/Sinpe)
     * @param totalPedidos  cantidad total de cobros del dia
     * @param totalVendido  total vendido del dia
     */
    public byte[] generarReporteDia(List<LineaReporte> lineas,
                                    int totalPedidos, BigDecimal totalVendido) {

        Document doc = new Document(PageSize.A5);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font fNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font fBold   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font fBlanco = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, java.awt.Color.WHITE);

        try {
            PdfWriter.getInstance(doc, salida);
            doc.open();

            // Encabezado
            Paragraph titulo = new Paragraph("Soda Candy", fTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph sub = new Paragraph("Reporte de ventas del dia", fBold);
            sub.setAlignment(Element.ALIGN_CENTER);
            doc.add(sub);

            Paragraph fecha = new Paragraph("Generado: " + LocalDateTime.now().format(FMT), fNormal);
            fecha.setAlignment(Element.ALIGN_CENTER);
            doc.add(fecha);

            doc.add(new Paragraph(" ", fNormal));

            // Tabla por metodo
            PdfPTable tabla = new PdfPTable(new float[]{3f, 2f, 3f});
            tabla.setWidthPercentage(100);
            tabla.addCell(celdaCab("Metodo de pago", fBlanco, Element.ALIGN_LEFT));
            tabla.addCell(celdaCab("# Ventas", fBlanco, Element.ALIGN_CENTER));
            tabla.addCell(celdaCab("Total", fBlanco, Element.ALIGN_RIGHT));

            for (LineaReporte l : lineas) {
                tabla.addCell(celda(nombreBonito(l.getMetodo()), fNormal, Element.ALIGN_LEFT));
                tabla.addCell(celda(String.valueOf(l.getCantidad()), fNormal, Element.ALIGN_CENTER));
                tabla.addCell(celda(SIMBOLO + l.getTotal(), fNormal, Element.ALIGN_RIGHT));
            }
            doc.add(tabla);

            doc.add(new Paragraph(" ", fNormal));

            // Totales del dia
            Paragraph pedidos = new Paragraph("Pedidos cobrados hoy: " + totalPedidos, fBold);
            doc.add(pedidos);
            Paragraph total = new Paragraph("Ventas del dia: " + SIMBOLO + totalVendido, fBold);
            doc.add(total);

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el reporte PDF", e);
        }
        return salida.toByteArray();
    }

    // Muestra el metodo lindo (EFECTIVO -> Efectivo)
    private String nombreBonito(String metodo) {
        if (metodo == null || metodo.isEmpty()) return "";
        if ("SINPE_MOVIL".equals(metodo)) return "Sinpe Movil";
        return metodo.charAt(0) + metodo.substring(1).toLowerCase();
    }

    private PdfPCell celdaCab(String txt, Font f, int align) {
        PdfPCell c = new PdfPCell(new Paragraph(txt, f));
        c.setBackgroundColor(new java.awt.Color(89, 2, 2));
        c.setHorizontalAlignment(align);
        c.setPadding(5);
        return c;
    }
    private PdfPCell celda(String txt, Font f, int align) {
        PdfPCell c = new PdfPCell(new Paragraph(txt, f));
        c.setHorizontalAlignment(align);
        c.setPadding(5);
        return c;
    }
}