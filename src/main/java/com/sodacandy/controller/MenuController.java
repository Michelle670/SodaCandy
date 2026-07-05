//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;
import com.sodacandy.repository.CategoriaRepository;
import com.sodacandy.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
/**
 * Menu publico del restaurante. Decision del proyecto: se ve SIN login.
 * En SecurityConfig la ruta /menu/** esta como permitAll().
 * Es solo lectura: lista las categorias activas y sus platillos disponibles.
 */
//==============================================================================
@Controller
@RequestMapping("/menu")
public class MenuController
{
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public MenuController(CategoriaRepository categoriaRepository, ProductoRepository productoRepository)
    {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping("/listado")
    public String listado(Model model)
    {
        //Solo lo que el cliente deberia ver: categorias activas y platillos disponibles
        model.addAttribute("categorias", categoriaRepository.findByActivoTrue());
        model.addAttribute("productos", productoRepository.findByActivoTrue());
        model.addAttribute("seccion", "menu");
        return "/menu/listado";
    }
}
