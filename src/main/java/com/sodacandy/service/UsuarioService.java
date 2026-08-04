//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.service;
import com.sodacandy.domain.Rol;
import com.sodacandy.domain.Usuario;
import com.sodacandy.domain.UsuarioRol;
import com.sodacandy.repository.RolRepository;
import com.sodacandy.repository.UsuarioRepository;
import com.sodacandy.repository.UsuarioRolRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    //Se agrega el PasswordEncoder para poder encriptar la clave con BCrypt (HU login)
    private final PasswordEncoder passwordEncoder;
    //Se agregan para poder asignar y consultar el rol del empleado
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolRepository rolRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            UsuarioRolRepository usuarioRolRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRolRepository = usuarioRolRepository;
        this.rolRepository = rolRepository;
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
    // La clave siempre se guarda encriptada con BCrypt, nunca en texto plano
    @Transactional
    public void save(Usuario usuario) {
        Optional<Usuario> existente = usuarioRepository.findByUsername(usuario.getUsername());
        if (existente.isPresent()
                && !existente.get().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalStateException("Ya existe un empleado con ese nombre de usuario.");
        }
        if (usuario.getIdUsuario() == null) {
            //Empleado nuevo: la clave es obligatoria y se encripta antes de guardar
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para nuevos empleados.");
            }
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                //No escribieron clave nueva: se conserva el hash que ya estaba guardado
                Usuario actual = usuarioRepository.findById(usuario.getIdUsuario())
                        .orElseThrow(() -> new IllegalArgumentException("Empleado a modificar no encontrado."));
                usuario.setPassword(actual.getPassword());
            } else {
                //Escribieron una clave nueva: se encripta antes de guardar
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
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

    //Asigna (o cambia) el rol de un empleado. Como cada empleado tiene un solo
    //rol desde el dropdown, primero se borra el que tuviera antes.
    @Transactional
    public void asignarRol(Integer idUsuario, Integer idRol) {
        usuarioRolRepository.deleteByIdUsuario(idUsuario);
        if (idRol != null) {
            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setIdUsuario(idUsuario);
            usuarioRol.setIdRol(idRol);
            usuarioRolRepository.save(usuarioRol);
        }
    }

    //Devuelve el id del rol actual del empleado (para preseleccionarlo en el dropdown al editar)
    @Transactional(readOnly = true)
    public Integer getIdRolUsuario(Integer idUsuario) {
        return usuarioRolRepository.findByIdUsuario(idUsuario).stream()
                .map(UsuarioRol::getIdRol)
                .findFirst()
                .orElse(null);
    }

    //Devuelve el nombre del rol actual del empleado (para mostrarlo en el listado)
    @Transactional(readOnly = true)
    public String getNombreRolUsuario(Integer idUsuario) {
        return rolRepository.findRolesByUsuario(idUsuario).stream()
                .map(Rol::getRol)
                .findFirst()
                .orElse(null);
    }
}