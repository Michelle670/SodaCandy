//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.service;
import com.sodacandy.domain.Usuario;
import com.sodacandy.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
//Servicio de "olvide mi contraseña": genera el token, lo manda por correo y valida el cambio de clave
@Service
public class RecuperarClaveService {

    //El enlace de recuperacion vence en 30 minutos
    private static final int MINUTOS_VIGENCIA = 30;
    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    private final UsuarioRepository usuarioRepository;
    private final CorreoService correoService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    @Value("${servidor.http}")
    private String servidor;

    public RecuperarClaveService(UsuarioRepository usuarioRepository,
            CorreoService correoService, PasswordEncoder passwordEncoder,
            MessageSource messageSource) {
        this.usuarioRepository = usuarioRepository;
        this.correoService = correoService;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    //Paso 1: el empleado pide el enlace desde /acceso/recuperar
    @Transactional
    public boolean solicitar(String username, String correo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndCorreo(username, correo);
        if (usuarioOpt.isEmpty()) {
            return false; //no existe esa combinacion de usuario/correo
        }
        Usuario usuario = usuarioOpt.get();
        String token = generaToken();
        usuario.setTokenReset(token);
        usuario.setTokenResetExpira(LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA));
        usuarioRepository.save(usuario);
        try {
            enviarCorreoRecuperacion(usuario, token);
        } catch (MessagingException e) {
            return false;
        }
        return true;
    }

    //Paso 2: valida el token que llego por el enlace del correo
    @Transactional(readOnly = true)
    public boolean tokenValido(String username, String token) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndTokenReset(username, token);
        return usuarioOpt.isPresent()
                && usuarioOpt.get().getTokenResetExpira() != null
                && usuarioOpt.get().getTokenResetExpira().isAfter(LocalDateTime.now());
    }

    //Paso 3: guarda la nueva clave elegida por el empleado y borra el token (un solo uso)
    @Transactional
    public boolean cambiarClave(String username, String token, String nuevaClave) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndTokenReset(username, token);
        if (usuarioOpt.isEmpty() || !tokenValido(username, token)) {
            return false;
        }
        Usuario usuario = usuarioOpt.get();
        usuario.setPassword(passwordEncoder.encode(nuevaClave));
        usuario.setTokenReset(null);
        usuario.setTokenResetExpira(null);
        usuarioRepository.save(usuario);
        return true;
    }

    //Arma y manda el correo con el enlace de recuperacion
    private void enviarCorreoRecuperacion(Usuario usuario, String token) throws MessagingException {
        String enlace = servidor + "/acceso/recuperar/confirmar/" + usuario.getUsername() + "/" + token;
        String mensaje = messageSource.getMessage("acceso.correo.cuerpo", null, Locale.getDefault());
        mensaje = String.format(mensaje, usuario.getNombre(), MINUTOS_VIGENCIA, enlace);
        String asunto = messageSource.getMessage("acceso.correo.asunto", null, Locale.getDefault());
        correoService.enviarCorreoHtml(usuario.getCorreo(), asunto, mensaje);
    }

    //Genera un token aleatorio de 40 caracteres
    private String generaToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            token.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return token.toString();
    }
}