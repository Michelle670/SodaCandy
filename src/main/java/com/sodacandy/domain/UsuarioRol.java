//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import jakarta.persistence.*;
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
@Table(name="usuario_rol")
@IdClass(UsuarioRolPK.class)//llave primaria compuesta (id_usuario + id_rol)
public class UsuarioRol implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    private Integer idUsuario;

    @Id
    private Integer idRol;
}
