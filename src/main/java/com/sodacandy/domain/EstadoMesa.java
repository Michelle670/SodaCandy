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
 * Estado de una mesa segun la HU-15. Los nombres coinciden con el ENUM en MySQL.
 * Soda Candy no maneja reservas; el ciclo es LIBRE -> OCUPADA -> POR_COBRAR -> LIBRE.
 */
//==============================================================================
public enum EstadoMesa 
{
    LIBRE,
    OCUPADA,
    POR_COBRAR
}
