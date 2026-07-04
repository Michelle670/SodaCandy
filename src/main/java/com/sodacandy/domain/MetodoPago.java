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
 * Metodo de pago. Decision del proyecto: UN solo metodo por pedido (sin pagos mixtos).
 * Se usa en la entidad Pago. Los nombres coinciden EXACTAMENTE con el ENUM
 * de la columna metodo en la tabla pago de MySQL.
 */
//==============================================================================
public enum MetodoPago 
{
    EFECTIVO,
    TARJETA,
    SINPE_MOVIL
}
