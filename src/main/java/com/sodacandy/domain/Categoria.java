
package com.sodacandy.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name="categoria")
public class Categoria implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;
    //========================================================
    //Ej: Casados, Desayunos, Bebidas, Postres
    @Column(unique = true, nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    private String descripcion;
    //========================================================
    @Column(length = 1024)
    @Size(max = 1024)
    private String rutaImagen;
    //========================================================
    private boolean activo;
}
