package com.sodacandy.controller;

import com.sodacandy.domain.Producto;
import com.sodacandy.service.CategoriaService;
import com.sodacandy.service.ProductoService;
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

@Controller// Anotacion controller
@RequestMapping("/producto")

public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final MessageSource messageSource;

    public ProductoController(ProductoService productoService,
            CategoriaService categoriaService,
            MessageSource messageSource) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("categorias", categorias);
        return "/producto/listado";
    }

    @GetMapping("/modificar/{idProducto}")
    public String modificar(@PathVariable("idProducto") Integer idProducto,
            Model model, RedirectAttributes redirectAttributes) {
        Optional<Producto> opt = productoService.getProducto(idProducto);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("producto.error01", null, Locale.getDefault()));
            return "redirect:/producto/listado";
        }
        model.addAttribute("producto", opt.get());
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        return "/producto/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Producto producto, RedirectAttributes redirectAttributes) {
        productoService.save(producto);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/producto/listado";
    }

    // HU-08: Botón agotado/disponible
    @PostMapping("/toggleDisponible")
    public String toggleDisponible(@RequestParam Integer idProducto,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.actualizado";
        try {
            productoService.toggleDisponible(idProducto);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "producto.error01";
        } catch (Exception e) {
            titulo = "error";
            detalle = "producto.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/producto/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idProducto, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            productoService.delete(idProducto);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "producto.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "producto.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "producto.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/producto/listado";
    }
}
