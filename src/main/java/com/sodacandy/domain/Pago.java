//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
@Data
@Entity
@Table(name="pago")
public class Pago implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;
    //========================================================
    //Un solo pago por pedido (la FK id_pedido es unica en la BD)
    @NotNull
    private Integer idPedido;
    //========================================================
    //Monto cobrado; la BD valida monto > 0
    @Column(precision = 12, scale = 2)
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal monto;
    //========================================================
    //Metodo de pago: se reutiliza el mismo enum que el resto del sistema
    //(EFECTIVO / TARJETA / SINPE_MOVIL). Decision: un solo metodo por pedido.
    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    private MetodoPago metodo;
    //========================================================
    private LocalDateTime fecha;
}
