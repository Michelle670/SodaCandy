
package com.sodacandy.domain;

/**
 * Tipo de pedido segun el Avance 1 (entidad pedidos) y la HU-13.
 * Los nombres coinciden EXACTAMENTE con el ENUM de la columna tipo en MySQL.
 *   MESA        -> consumo en el local: id_mesa obligatorio; nombre/telefono van null
 *   PARA_LLEVAR -> mandadito: id_mesa va null; nombre_cliente y telefono obligatorios
 */
public enum TipoPedido {
    MESA,
    PARA_LLEVAR
}
