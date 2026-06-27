
package com.sodacandy.domain;

import java.io.Serializable;
import lombok.Data;

/**
 * Llave primaria compuesta para la tabla puente usuario_rol.
 * Se usa con @IdClass en la entidad UsuarioRol.
 * Lombok @Data genera equals() y hashCode() (obligatorios en una llave de JPA).
 */
@Data
public class UsuarioRolPK implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Integer idUsuario;
    private Integer idRol;
}
