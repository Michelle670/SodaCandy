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

    //La usa la seguridad: solo empleados activos pueden iniciar sesion
    public Optional<Usuario> findByUsernameAndActivoTrue(String username);

    //La usa "olvide mi contraseña" para validar usuario + correo antes de mandar el enlace
    public Optional<Usuario> findByUsernameAndCorreo(String username, String correo);

    //La usa para validar el token que llega en el enlace del correo
    public Optional<Usuario> findByUsernameAndTokenReset(String username, String tokenReset);
}