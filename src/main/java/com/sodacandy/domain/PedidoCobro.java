//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import java.math.BigDecimal;
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
 * Objeto de PRESENTACION para la pantalla de Cobro (no es @Entity, no toca BD).
 *
 * Junta lo que la pantalla de cobro necesita ya resuelto: el pedido, sus lineas
 * (con nombre de producto y subtotal) y el total. Lo arma el CobroController
 * combinando Pedido  + DetallePedido + Producto .
 */
//==============================================================================
@Data
@AllArgsConstructor
public class PedidoCobro {
    private Integer idPedido;
    private Integer idMesa;              // null si es PARA_LLEVAR
    private List<LineaCobro> lineas;
    private BigDecimal total;
}