//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.domain.Mesa;
import com.sodacandy.service.MesaService;
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
@RequestMapping("/mesa")
public class MesaController {

    private final MesaService mesaService;
    private final MessageSource messageSource;

    public MesaController(MesaService mesaService, MessageSource messageSource) {
        this.mesaService = mesaService;
        this.messageSource = messageSource;
    }

    // HU-15: panel con el estado de todas las mesas
    @GetMapping("/listado")
    public String listado(Model model) {
        var mesas = mesaService.getMesas(false);
        model.addAttribute("mesas", mesas);
        model.addAttribute("totalMesas", mesas.size());
        model.addAttribute("mesa", new Mesa());
        model.addAttribute("modulo", "mesas");
        return "/mesa/listado";
    }

    // Trae una mesa para editarla. Si el id no existe regresa al listado con error.
    @GetMapping("/modificar/{idMesa}")
    public String modificar(@PathVariable("idMesa") Integer idMesa,
            Model model, RedirectAttributes redirectAttributes) {
        Optional<Mesa> opt = mesaService.getMesa(idMesa);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("mesa.error01", null, Locale.getDefault()));
            return "redirect:/mesa/listado";
        }
        model.addAttribute("mesa", opt.get());
        model.addAttribute("modulo", "mesas");
        return "/mesa/modifica";
    }

    // Guarda una mesa nueva o actualiza una existente.
    @PostMapping("/guardar")
    public String guardar(@Valid Mesa mesa, RedirectAttributes redirectAttributes) {
        mesaService.save(mesa);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/mesa/listado";
    }

    // HU-11: abrir mesa (LIBRE -> OCUPADA)
    @PostMapping("/abrir")
    public String abrir(@RequestParam Integer idMesa, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mesa.abierta";
        try {
            mesaService.abrir(idMesa);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "mesa.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "mesa.error04";
        } catch (Exception e) {
            titulo = "error";
            detalle = "mesa.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/mesa/listado";
    }

    // Liberar mesa (-> LIBRE)
    @PostMapping("/liberar")
    public String liberar(@RequestParam Integer idMesa, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mesa.liberada";
        try {
            mesaService.liberar(idMesa);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "mesa.error01";
        } catch (Exception e) {
            titulo = "error";
            detalle = "mesa.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/mesa/listado";
    }

    // Elimina una mesa. Si tiene pedidos asociados no se deja borrar.
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idMesa, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            mesaService.delete(idMesa);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "mesa.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "mesa.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "mesa.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/mesa/listado";
    }
}
