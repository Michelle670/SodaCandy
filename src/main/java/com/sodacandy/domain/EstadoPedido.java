
package com.sodacandy.domain;

/**
 * Ciclo de vida de un pedido. Los nombres coinciden con el ENUM en MySQL.
 */
public enum EstadoPedido {
    PENDIENTE,
    EN_PREPARACION,
    SERVIDO,
    PAGADO,
    ANULADO
}
