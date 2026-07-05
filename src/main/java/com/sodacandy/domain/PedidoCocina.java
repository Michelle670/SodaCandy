//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
/**
 * Objeto de PRESENTACION para el tablero de cocina (no es @Entity, no toca BD).
 *
 * El modelo de Ronald guarda el Pedido y sus DetallePedido en tablas separadas,
 * y el detalle solo tiene idProducto (numero), no el nombre. Esta clase junta
 * todo lo que la tarjeta necesita ya resuelto, para que la vista quede limpia.
 *
 * Lo arma el CocinaController combinando: Pedido + DetallePedidoRepository +
 * ProductoService.
 */
//==============================================================================
@Data
@AllArgsConstructor
public class PedidoCocina {
    private Integer idPedido;
    private Integer idMesa;        // puede ser null si el pedido es PARA_LLEVAR
    private List<LineaCocina> lineas;
}
