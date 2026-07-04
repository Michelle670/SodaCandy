//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
/**
 * Ciclo de vida de un pedido. Los nombres coinciden con el ENUM en MySQL.
 */
//==============================================================================
public enum EstadoPedido 
{
    PENDIENTE,
    EN_PREPARACION,
    SERVIDO,
    PAGADO,
    ANULADO
}
