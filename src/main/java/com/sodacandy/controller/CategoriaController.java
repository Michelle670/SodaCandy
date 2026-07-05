//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.domain.Categoria;
import com.sodacandy.service.CategoriaService;
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
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final MessageSource messageSource;

    public CategoriaController(CategoriaService categoriaService, MessageSource messageSource) {
        this.categoriaService = categoriaService;
        this.messageSource = messageSource;
    }

    // Muestra la tabla con todas las categorías.
    @GetMapping("/listado")
    public String listado(Model model) {
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        model.addAttribute("modulo", "categorias");
        return "/categoria/listado";
    }

    // Trae los datos de una categoría puntual para llenar el formulario de edición.
    // Si el id no existe, regresa al listado con un mensaje de error.
    @GetMapping("/modificar/{idCategoria}")
    public String modificar(@PathVariable("idCategoria") Integer idCategoria,
            Model model, RedirectAttributes redirectAttributes) {
        Optional<Categoria> opt = categoriaService.getCategoria(idCategoria);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("categoria.error01", null, Locale.getDefault()));
            return "redirect:/categoria/listado";
        }
        model.addAttribute("categoria", opt.get());
        return "/categoria/modifica";
    }

    // Guarda una categoría nueva o actualiza una existente.
    @PostMapping("/guardar")
    public String guardar(@Valid Categoria categoria, RedirectAttributes redirectAttributes) {
        categoriaService.save(categoria);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/categoria/listado";
    }

    // Elimina una categoría. Si ya tiene productos asociados no se deja borrar.
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCategoria, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            categoriaService.delete(idCategoria);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "categoria.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "categoria.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "categoria.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/categoria/listado";
    }
}
