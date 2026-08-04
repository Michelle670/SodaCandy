//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.domain.Usuario;
import com.sodacandy.service.RolService;
import com.sodacandy.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
@Controller// Anotacion controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final MessageSource messageSource;

    public UsuarioController(UsuarioService usuarioService, RolService rolService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.messageSource = messageSource;
    }

    // HU-02: gestion de empleados (listado)
    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.getUsuarios();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("modulo", "empleados");
        //Roles disponibles para el dropdown del modal "Agregar"
        model.addAttribute("roles", rolService.getRoles());
        //Rol actual de cada empleado, para mostrarlo en la columna Rol de la tabla
        Map<Integer, String> rolesPorUsuario = new HashMap<>();
        for (Usuario u : usuarios) {
            rolesPorUsuario.put(u.getIdUsuario(), usuarioService.getNombreRolUsuario(u.getIdUsuario()));
        }
        model.addAttribute("rolesPorUsuario", rolesPorUsuario);
        return "/usuario/listado";
    }

    // Trae un empleado para editarlo. Si el id no existe regresa al listado con error.
    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable("idUsuario") Integer idUsuario,
            Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> opt = usuarioService.getUsuario(idUsuario);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("usuario.error01", null, Locale.getDefault()));
            return "redirect:/usuario/listado";
        }
        model.addAttribute("usuario", opt.get());
        model.addAttribute("modulo", "empleados");
        //Roles disponibles para el dropdown, y el rol actual para preseleccionarlo
        model.addAttribute("roles", rolService.getRoles());
        model.addAttribute("idRolActual", usuarioService.getIdRolUsuario(idUsuario));
        return "/usuario/modifica";
    }

    // Guarda un empleado nuevo o actualiza uno existente, junto con el rol elegido en el dropdown.
    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario,
            @RequestParam(required = false) Integer idRol,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.actualizado";
        try {
            usuarioService.save(usuario);
            //Al guardar, JPA ya dejo el id generado dentro de "usuario" (sea nuevo o existente)
            usuarioService.asignarRol(usuario.getIdUsuario(), idRol);
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "usuario.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "usuario.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }

    // HU-02: desactivar empleado (no se elimina)
    @PostMapping("/desactivar")
    public String desactivar(@RequestParam Integer idUsuario, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "usuario.desactivado";
        try {
            usuarioService.desactivar(idUsuario);
        } catch (Exception e) {
            titulo = "error";
            detalle = "usuario.error01";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }

    // Reactivar empleado
    @PostMapping("/activar")
    public String activar(@RequestParam Integer idUsuario, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "usuario.activado";
        try {
            usuarioService.activar(idUsuario);
        } catch (Exception e) {
            titulo = "error";
            detalle = "usuario.error01";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }

    // Elimina un empleado de forma permanente.
    // Si ya tiene pedidos asociados no se deja borrar (mejor usar desactivar).
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idUsuario, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            usuarioService.delete(idUsuario);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "usuario.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "usuario.error04";
        } catch (Exception e) {
            titulo = "error";
            detalle = "usuario.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }
}