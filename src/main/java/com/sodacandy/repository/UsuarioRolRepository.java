//==============================================================================
// IMPORTES
//==============================================================================
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

    //Borra el/los roles que ya tenia el empleado, para asignarle uno nuevo desde el dropdown
    public void deleteByIdUsuario(Integer idUsuario);
}