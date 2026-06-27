
package com.sodacandy.domain;

/**
 * Estado de una mesa segun la HU-15. Los nombres coinciden con el ENUM en MySQL.
 * Soda Candy no maneja reservas; el ciclo es LIBRE -> OCUPADA -> POR_COBRAR -> LIBRE.
 */
public enum EstadoMesa {
    LIBRE,
    OCUPADA,
    POR_COBRAR
}
