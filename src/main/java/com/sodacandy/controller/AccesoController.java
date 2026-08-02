//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.service.RecuperarClaveService;
import java.util.Locale;
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
@Controller
@RequestMapping("/acceso")
public class AccesoController {

    private final RecuperarClaveService recuperarClaveService;
    private final MessageSource messageSource;

    public AccesoController(RecuperarClaveService recuperarClaveService, MessageSource messageSource) {
        this.recuperarClaveService = recuperarClaveService;
        this.messageSource = messageSource;
    }

    //Muestra el formulario para pedir el enlace de recuperacion
    @GetMapping("/recuperar")
    public String formularioRecuperar() {
        return "/acceso/recuperar";
    }

    //Procesa el formulario: valida usuario+correo y manda el correo con el enlace
    @PostMapping("/recuperar")
    public String solicitarRecuperar(@RequestParam String username,
            @RequestParam String correo, Model model) {
        boolean enviado = recuperarClaveService.solicitar(username, correo);
        model.addAttribute("titulo", messageSource.getMessage("acceso.recuperar.titulo", null, Locale.getDefault()));
        model.addAttribute("mensaje", messageSource.getMessage(
                enviado ? "acceso.recuperar.enviado" : "acceso.recuperar.error",
                null, Locale.getDefault()));
        return "/acceso/salida";
    }

    //Se abre desde el enlace del correo: valida el token y muestra el formulario de nueva clave
    @GetMapping("/recuperar/confirmar/{username}/{token}")
    public String formularioNuevaClave(@PathVariable String username,
            @PathVariable String token, Model model, RedirectAttributes redirectAttributes) {
        if (!recuperarClaveService.tokenValido(username, token)) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("acceso.token.invalido", null, Locale.getDefault()));
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        model.addAttribute("token", token);
        return "/acceso/nuevaClave";
    }

    //Guarda la nueva clave que escribio el empleado
    @PostMapping("/recuperar/confirmar")
    public String guardarNuevaClave(@RequestParam String username,
            @RequestParam String token, @RequestParam String password,
            @RequestParam String confirmarPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmarPassword)) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("acceso.nuevaClave.error.noCoincide", null, Locale.getDefault()));
            return "redirect:/acceso/recuperar/confirmar/" + username + "/" + token;
        }

        boolean actualizado = recuperarClaveService.cambiarClave(username, token, password);
        if (!actualizado) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("acceso.token.invalido", null, Locale.getDefault()));
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("acceso.nuevaClave.ok", null, Locale.getDefault()));
        return "redirect:/login";
    }
}