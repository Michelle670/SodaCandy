//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
/**
 * Una linea del pedido, lista para pintar en la tarjeta de cocina.
 * El nombre del producto ya viene resuelto (buscado por idProducto), asi la
 * vista solo lo muestra.
 */
//==============================================================================
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineaCocina {
    private int cantidad;
    private String nombreProducto;   // resuelto desde ProductoService (o "Producto #id" si no se encuentra)
    private String nota;             // HU-21: alergia/instruccion; null si no aplica
}
