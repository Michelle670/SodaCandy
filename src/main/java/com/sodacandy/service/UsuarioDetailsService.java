//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.service;
import com.sodacandy.domain.Usuario;
import com.sodacandy.repository.RolRepository;
import com.sodacandy.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
//Este servicio lo llama Spring Security automaticamente al hacer login
@Service("userDetailsService")
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final HttpSession session;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository, HttpSession session) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.session = session;
    }

    //Busca el usuario por username, valida que este activo y le arma sus roles de seguridad
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo: " + username));

        //Se guarda en sesion el nombre, para mostrarlo despues en el shell de staff
        session.removeAttribute("nombreUsuario");
        session.setAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellidos());
        session.setAttribute("imagenUsuario", usuario.getRutaImagen());

        var roles = rolRepository.findRolesByUsuario(usuario.getIdUsuario()).stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getRol()))
                .collect(Collectors.toSet());

        return new User(usuario.getUsername(), usuario.getPassword(), roles);
    }
}