//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name="producto")
public class Producto implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;
    //Se enlaza con la categoria a la que pertenece el platillo
    private Integer idCategoria;
    //========================================================
    @Column(unique = true, nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String descripcion;
    //========================================================
    @Column(columnDefinition = "TEXT")
    private String detalle;
    //========================================================
    @Column(precision = 12, scale = 2)
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal precio;
    //========================================================
    //En un restaurante interesa si el platillo esta disponible hoy
    private boolean disponible;
    //========================================================
    @Column(length = 1024)
    @Size(max = 1024)
    private String rutaImagen;
    //========================================================
    private boolean activo;
}
