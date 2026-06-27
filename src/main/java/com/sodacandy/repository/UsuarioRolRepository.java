
package com.sodacandy.repository;

import com.sodacandy.domain.UsuarioRol;
import com.sodacandy.domain.UsuarioRolPK;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolPK>
{
    public List<UsuarioRol> findByIdUsuario(Integer idUsuario);
}
