
package com.sodacandy.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name="pedido")
public class Pedido implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;
    //========================================================
    //Empleado (salonero) que tomo el pedido (FK obligatoria)
    @NotNull
    private Integer idUsuario;
    //Mesa atendida; va nula cuando el pedido es PARA_LLEVAR
    private Integer idMesa;
    //========================================================
    //Tipo de pedido segun HU-13: MESA o PARA_LLEVAR
    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    private TipoPedido tipo;
    //========================================================
    //HU-13: en pedidos PARA_LLEVAR se capturan como campos del propio pedido
    //(NO hay tabla cliente). En pedidos de MESA van null.
    @Column(name = "nombre_cliente", length = 100)
    @Size(max = 100)
    private String nombreCliente;

    @Column(length = 25)
    @Size(max = 25)
    private String telefono;
    //========================================================
    private LocalDateTime fecha;
    //========================================================
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private EstadoPedido estado;
    //========================================================
    //El metodo de pago NO va aqui: vive en la entidad Pago (modelo del Avance 1)
    @Column(precision = 12, scale = 2)
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal total;
}
