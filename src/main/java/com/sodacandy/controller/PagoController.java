//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.domain.MetodoPago;
import com.sodacandy.domain.Pago;
import com.sodacandy.service.PagoService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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
/**
 * Controlador de Pago.
 *   - @Controller con @RequestMapping("/pago")
 *   - Inyeccion por constructor de Service + MessageSource
 *   - 4 endpoints estandar:
 *       GET  /pago/listado         -> lista todos
 *       GET  /pago/modificar/{id}  -> formulario de edicion
 *       POST /pago/guardar         -> crear o actualizar
 *       POST /pago/eliminar        -> eliminar
 */
//==============================================================================
@Controller
@RequestMapping("/pago")
public class PagoController {

    private final PagoService pagoService;
    private final MessageSource messageSource;

    public PagoController(PagoService pagoService, MessageSource messageSource) {
        this.pagoService = pagoService;
        this.messageSource = messageSource;
    }

    //==========================================================
    // GET /pago/listado - Lista de pagos
    //==========================================================
    @GetMapping("/listado")
    public String listado(Model model) {
        var pagos = pagoService.getPagos();
        model.addAttribute("pagos", pagos);
        model.addAttribute("totalPagos", pagos.size());
        // Objeto vacio para el formulario del modal "agregar"
        model.addAttribute("pago", new Pago());
        // Valores del enum para el <select> de metodo
        model.addAttribute("metodos", MetodoPago.values());
        return "pago/listado";
    }

    //==========================================================
    // GET /pago/modificar/{id} - Formulario de edicion
    //==========================================================
    @GetMapping("/modificar/{idPago}")
    public String modificar(@PathVariable("idPago") Integer idPago,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        Optional<Pago> pagoOpt = pagoService.getPago(idPago);
        if (pagoOpt.isEmpty()) {
            // No se encontro el pago
            redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("pago.error01", null, Locale.getDefault()));
            return "redirect:/pago/listado";
        }
        model.addAttribute("pago", pagoOpt.get());
        model.addAttribute("metodos", MetodoPago.values());
        return "pago/modifica";
    }

    //==========================================================
    // POST /pago/guardar - Crear o actualizar
    //==========================================================
    @PostMapping("/guardar")
    public String guardar(@Valid Pago pago, RedirectAttributes redirectAttributes) {
        // Unico ajuste sobre el molde Rol: la fecha del pago NO la digita el
        // usuario, se sella en el servidor.
        //   - Nuevo pago -> se estampa la fecha/hora del cobro (ahora)
        //   - Edicion    -> se conserva la fecha original del registro
        if (pago.getIdPago() == null) {
            pago.setFecha(LocalDateTime.now());
        } else {
            pagoService.getPago(pago.getIdPago())
                       .ifPresent(original -> pago.setFecha(original.getFecha()));
        }
        pagoService.save(pago);
        redirectAttributes.addFlashAttribute("todoOk",
            messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/pago/listado";
    }

    //==========================================================
    // POST /pago/eliminar - Eliminar pago 
    //==========================================================
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idPago, RedirectAttributes redirectAttributes) {
        try {
            pagoService.delete(idPago);
            redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.eliminado", null, Locale.getDefault()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            // El servicio ya lanza el mensaje listo (no existe / tiene datos asociados)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pago/listado";
    }
}