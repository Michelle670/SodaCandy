//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Data
@Entity
@Table(name="usuario")
public class Usuario implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    //========================================================
    @Column(unique = true, nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String username;
    //========================================================
    //Se guarda el hash BCrypt; por eso 512 de largo
    @Column(nullable = false, length = 512)
    @NotNull
    private String password;
    //========================================================
    @Column(nullable = false, length = 20)
    @NotNull
    @Size(max = 20)
    private String nombre;
    //========================================================
    @Column(nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String apellidos;
    //========================================================
    @Column(unique = true, length = 75)
    @Email
    @Size(max = 75)
    private String correo;
    //========================================================
    @Column(length = 25)
    @Size(max = 25)
    private String telefono;
    //========================================================
    @Column(length = 1024)
    @Size(max = 1024)
    private String rutaImagen;
    //========================================================
    private boolean activo;
}
