
package com.sodacandy.repository;

import com.sodacandy.domain.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>
{
    //Consulta derivada usada por la seguridad para autenticar
    public Optional<Usuario> findByUsername(String username);
}
