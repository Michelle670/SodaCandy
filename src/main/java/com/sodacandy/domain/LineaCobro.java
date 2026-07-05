package com.sodacandy.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Una linea del pedido lista para el cobro y el tiquete:
 * cantidad, nombre del platillo, precio unitario y subtotal ya calculado.
 */
@Data
@AllArgsConstructor
public class LineaCobro {
    private int cantidad;
    private String nombreProducto;      // = Producto.descripcion 
    private BigDecimal precioUnitario;  // = DetallePedido.precioHistorico 
    private BigDecimal subtotal;        // cantidad * precioUnitario
}