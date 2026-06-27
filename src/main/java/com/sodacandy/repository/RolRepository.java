
package com.sodacandy.repository;

import com.sodacandy.domain.Rol;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer>
{
    //Recupera los roles asociados a un usuario (lo usa la seguridad)
    @Query("SELECT r FROM Rol r, UsuarioRol ur "
         + "WHERE ur.idRol = r.idRol AND ur.idUsuario = :idUsuario")
    public List<Rol> findRolesByUsuario(@Param("idUsuario") Integer idUsuario);
}
