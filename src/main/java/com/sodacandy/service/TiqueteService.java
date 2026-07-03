//==============================================================================
// IMPORTES
//==============================================================================
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
import com.sodacandy.domain.LineaCobro;
import com.sodacandy.domain.PedidoCobro;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
/*
 * ============================================================================
 * TiqueteService — genera el tiquete de cobro en PDF (HU-19).
 * INVESTIGACION ADICIONAL: generacion de documentos PDF en Spring Boot.
 * ============================================================================
 *
 * POR QUE SE USA UNA LIBRERIA:
 *   Un PDF no es texto plano, es un formato binario con estructura interna
 *   (objetos, tabla de referencias, streams). Generarlo a mano es inviable;
 *   la practica estandar en Java es usar una libreria y solo describir el
 *   contenido (titulos, tablas, totales).
 *
 * LIBRERIA ELEGIDA: OpenPDF (com.github.librepdf:openpdf).
 *
 * COMPARACION DE OPCIONES:
 *   - iText 5 / 7  -> licencia AGPL: obligaria a liberar todo el codigo del
 *                     proyecto o pagar licencia comercial. DESCARTADA (riesgo legal).
 *   - Apache PDFBox -> libre (Apache 2.0) pero de BAJO NIVEL: hay que dibujar
 *                      por coordenadas x/y. Mucho codigo para un tiquete simple.
 *   - OpenPDF      -> ELEGIDA. Fork libre del iText 4 clasico, licencia
 *                     LGPL/MPL (sin ataduras), API de ALTO NIVEL (Paragraph,
 *                     PdfPTable, PdfPCell) y una sola dependencia liviana.
 *
 * RESUMEN: OpenPDF gana por licencia libre + API sencilla + poco peso.
 *
 * Nota tecnica: los paquetes se llaman "com.lowagie.text.*" por herencia del
 * iText clasico. Es normal, no es un error.
 * ============================================================================
 */
@Service
public class TiqueteService {

    private static final String SIMBOLO = "\u20A1"; // colon costarricense
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Construye el PDF del tiquete y lo devuelve como arreglo de bytes,
     * listo para descargar desde el controlador.
     *
     * @param pedido  datos del pedido cobrado (lineas + total)
     * @param metodo  metodo de pago usado (EFECTIVO / TARJETA / SINPE_MOVIL)
     * @return        el PDF en bytes
     */
    public byte[] generarTiquete(PedidoCobro pedido, String metodo) {
        Document doc = new Document(PageSize.A6);   // tamano tipo tiquete
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font fNormal = FontFactory.getFont(FontFactory.HELVETICA, 9);
        Font fBold   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        try {
            PdfWriter.getInstance(doc, salida);
            doc.open();

            // ----- Encabezado -----
            Paragraph titulo = new Paragraph("Soda Candy", fTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph sub = new Paragraph("Puriscal, Costa Rica\nTiquete de cobro", fNormal);
            sub.setAlignment(Element.ALIGN_CENTER);
            doc.add(sub);

            doc.add(new Paragraph(" ", fNormal));

            // ----- Datos del pedido -----
            doc.add(new Paragraph("Pedido #" + pedido.getIdPedido(), fBold));
            String mesa = (pedido.getIdMesa() != null)
                    ? "Mesa: " + pedido.getIdMesa()
                    : "Para llevar";
            doc.add(new Paragraph(mesa, fNormal));
            doc.add(new Paragraph("Fecha: " + LocalDateTime.now().format(FMT), fNormal));
            doc.add(new Paragraph("Metodo: " + metodo, fNormal));

            doc.add(new Paragraph(" ", fNormal));

            // ----- Tabla de lineas -----
            PdfPTable tabla = new PdfPTable(new float[]{1f, 4f, 2.2f});
            tabla.setWidthPercentage(100);
            tabla.addCell(celdaCab("Cant", fBold));
            tabla.addCell(celdaCab("Producto", fBold));
            tabla.addCell(celdaCabDer("Subtotal", fBold));

            for (LineaCobro l : pedido.getLineas()) {
                tabla.addCell(celda(String.valueOf(l.getCantidad()), fNormal));
                tabla.addCell(celda(l.getNombreProducto(), fNormal));
                tabla.addCell(celdaDer(SIMBOLO + l.getSubtotal(), fNormal));
            }
            doc.add(tabla);

            // ----- Total -----
            Paragraph total = new Paragraph("TOTAL: " + SIMBOLO + pedido.getTotal(), fBold);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(8);
            doc.add(total);

            doc.add(new Paragraph(" ", fNormal));
            Paragraph gracias = new Paragraph("!Gracias por su visita!", fNormal);
            gracias.setAlignment(Element.ALIGN_CENTER);
            doc.add(gracias);

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el tiquete PDF", e);
        }
        return salida.toByteArray();
    }

    // ---------- helpers de celdas ----------
    // Encabezado: fondo vino + texto BLANCO (se ignora el color del font recibido)
    private PdfPCell celdaCab(String txt, Font base) {
        Font blanco = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, java.awt.Color.WHITE);
        PdfPCell c = new PdfPCell(new Paragraph(txt, blanco));
        c.setBackgroundColor(new java.awt.Color(89, 2, 2));
        c.setPadding(4);
        return c;
    }
    private PdfPCell celdaCabDer(String txt, Font f) {
        PdfPCell c = celdaCab(txt, f);
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return c;
    }
    private PdfPCell celda(String txt, Font f) {
        PdfPCell c = new PdfPCell(new Paragraph(txt, f));
        c.setPadding(4);
        return c;
    }
    private PdfPCell celdaDer(String txt, Font f) {
        PdfPCell c = celda(txt, f);
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return c;
    }
}