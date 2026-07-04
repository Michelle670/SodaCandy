//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.domain.Rol;
import com.sodacandy.service.RolService;
import jakarta.validation.Valid;
import java.util.Locale;
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
@RequestMapping("/rol")// todo lo que se pida bajo /rol lo resuelve este codigo
public class RolController
{
    //Para enlazar el servicio del rol y los mensajes i18n
    private final RolService rolService;
    private final MessageSource messageSource;

    public RolController(RolService rolService, MessageSource messageSource)
    {
        this.rolService = rolService;
        this.messageSource = messageSource;
    }

    //metodo del listado
    @GetMapping("/listado")
    public String listado(Model model)
    {
        var roles = rolService.getRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("totalRoles", roles.size());
        return "/rol/listado";
    }

    //metodo que abre la pagina de edicion
    @GetMapping("/modificar/{idRol}")
    public String modificar(@PathVariable("idRol") Integer idRol,
            Model model,
            RedirectAttributes redirectAttributes)
    {
        Optional<Rol> rolOpt = rolService.getRol(idRol);
        if (rolOpt.isEmpty()) { // si no lo encontraron...
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("rol.error01", null, Locale.getDefault()));
            return "redirect:/rol/listado";
        }
        model.addAttribute("rol", rolOpt.get());
        return "/rol/modifica";
    }

    //metodo que inserta o actualiza (segun venga o no el idRol)
    @PostMapping("/guardar")
    public String guardar(@Valid Rol rol, RedirectAttributes redirectAttributes)
    {
        rolService.save(rol);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/rol/listado";
    }

    //metodo que elimina
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idRol, RedirectAttributes redirectAttributes)
    {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            rolService.delete(idRol);
        } catch (IllegalArgumentException e) {
            titulo = "error"; // el rol no existe
            detalle = "rol.error01";
        } catch (IllegalStateException e) {
            titulo = "error"; // el rol tiene usuarios asociados
            detalle = "rol.error02";
        } catch (Exception e) {
            titulo = "error"; // cualquier otro error inesperado
            detalle = "rol.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/rol/listado";
    }
}
