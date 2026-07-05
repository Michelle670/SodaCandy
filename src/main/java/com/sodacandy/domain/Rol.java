//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
@Data// hace los set y get y no hay que escribirlo
@Entity// lo declara como entidad
@Table(name="rol")//conecta con la tabla y se pone el nombre de la tabla NO de la clase
public class Rol implements Serializable// que se pueden enviar datos en serie
{
    //se establece un inicio de ids para serializar
    private static final long serialVersionUID = 1L;

    //Anotaciones para id
    @Id
    //para que sea autoincremental
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;
    //========================================================
    //Anotaciones para el nombre del rol
    //Que sea unico, no nulo, y que no se pase de 20 caracteres
    @Column(unique = true, nullable = false, length = 20)
    //Validaciones antes de enviar a BD
    @NotNull
    @Size(max = 20)
    private String rol;
    //========================================================
}
