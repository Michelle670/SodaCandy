
package com.sodacandy.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name="detalle_pedido")
public class DetallePedido implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalle;
    //========================================================
    @NotNull
    private Integer idPedido;//FK al pedido
    @NotNull
    private Integer idProducto;//FK al producto
    //========================================================
    @Min(value = 1)
    private Integer cantidad;
    //========================================================
    //Se guarda el precio al momento de la venta (no cambia si luego sube el menu)
    @Column(name = "precio_historico", precision = 12, scale = 2)
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal precioHistorico;
    //========================================================
    //HU-21: nota por linea para alergias / modificaciones
    //  (ej: "sin cebolla", "alergia al mani", "aderezo aparte")
    @Column(length = 255)
    @Size(max = 255)
    private String nota;
}
