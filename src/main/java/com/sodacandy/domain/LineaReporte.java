//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
@Data
@AllArgsConstructor
public class LineaReporte 
{
    private String metodo;      // EFECTIVO / TARJETA / SINPE_MOVIL
    private int cantidad;       // cuantas ventas por ese metodo
    private BigDecimal total;   // suma de montos
}