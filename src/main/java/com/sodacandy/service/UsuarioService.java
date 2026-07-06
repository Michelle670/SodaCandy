//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.service;
import com.sodacandy.domain.Usuario;
import com.sodacandy.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================

//Siempre se hace la anotacion @Service en la capa de servicio del MVC
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Devuelve todos los empleados
    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    // READ
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    // CREATE / UPDATE
    // HU-02: no se permiten usernames duplicados
    @Transactional
    public void save(Usuario usuario) {
        Optional<Usuario> existente = usuarioRepository.findByUsername(usuario.getUsername());
        if (existente.isPresent()
                && !existente.get().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalStateException("Ya existe un empleado con ese nombre de usuario.");
        }
        usuarioRepository.save(usuario);
    }

    // HU-02: desactivar empleado (no se elimina fisicamente)
    @Transactional
    public void desactivar(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado id " + idUsuario));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    // Reactivar empleado
    @Transactional
    public void activar(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado id " + idUsuario));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    // DELETE
    @Transactional
    public void delete(Integer idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("No se puede eliminar el registro, no existe id " + idUsuario);
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se elimina porque tiene datos asociados.");
        }
    }
}
