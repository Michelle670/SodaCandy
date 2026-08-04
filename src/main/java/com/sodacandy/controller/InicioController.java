//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
/**
 * Controlador de la pantalla de inicio del STAFF (home interna).
 * Sigue el patron del curso: @Controller + inyeccion por constructor.
 *
 * Ruta:  GET /home  ->  templates/inicio.html
 *
 * No usa Service porque no consulta ninguna entidad: solo arma datos de
 * presentacion (nombre, fecha y turno). El "nombreUsuario" se lee de la
 * sesion, que UsuarioDetailsService llena al momento del login.
 */
//==============================================================================
@Controller
public class InicioController {
    // MessageSource para resolver el texto del turno en el idioma activo 
    private final MessageSource messageSource;
    public InicioController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    @GetMapping("/home")
    public String home(Model model, Locale locale, HttpSession session) {
        // 1) Nombre a saludar: se lee de la sesion (lo guarda UsuarioDetailsService al hacer login)
        Object nombreSesion = session.getAttribute("nombreUsuario");
        model.addAttribute("nombreUsuario", nombreSesion != null ? nombreSesion : "Staff");
        // 2) Fecha actual, localizada segun el idioma (es -> "jueves, 2 de julio de 2026")
        String fecha = LocalDate.now()
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale));
        // Capitaliza la primera letra (los dias en español salen en minuscula)
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);
        model.addAttribute("fecha", fecha);
        // 3) Turno segun la hora del servidor (manana / tarde / noche)
        int hora = LocalTime.now().getHour();
        String turnoKey = (hora < 12) ? "inicio.turno.manana"
                        : (hora < 18) ? "inicio.turno.tarde"
                        :               "inicio.turno.noche";
        model.addAttribute("turno", messageSource.getMessage(turnoKey, null, locale));
        return "inicio";
    }
}