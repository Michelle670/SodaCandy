//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
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
@Table(name="mesa")
public class Mesa implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMesa;
    //========================================================
    //Numero visible de la mesa en el local (unico)
    @Column(unique = true, nullable = false)
    @NotNull
    @Min(value = 1)
    private Integer numero;
    //========================================================
    @Min(value = 1)
    private Integer capacidad;
    //========================================================
    //Se mapea el ENUM de la BD como String del enum (LIBRE/OCUPADA/POR_COBRAR)
    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    private EstadoMesa estado;
    //========================================================
    private boolean activo;
}
