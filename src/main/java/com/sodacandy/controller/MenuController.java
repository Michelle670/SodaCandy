//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.controller;

import com.sodacandy.domain.Producto;
import com.sodacandy.repository.CategoriaRepository;
import com.sodacandy.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
 * Soporta filtrado por categoria igual que el modulo de pedidos.
 */
//==============================================================================
@Controller
@RequestMapping("/menu")
public class MenuController
{
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public MenuController(CategoriaRepository categoriaRepository,
                          ProductoRepository productoRepository)
    {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository  = productoRepository;
    }

    // Muestra el menu publico con filtro opcional por categoria.
    // idCategoria=0 significa "Todos".
    @GetMapping("/listado")
    public String listado(@RequestParam(defaultValue = "0") int idCategoria,
                          Model model)
    {
        //Solo lo que el cliente deberia ver: categorias activas y platillos disponibles
        model.addAttribute("categorias", categoriaRepository.findByActivoTrue());

        // Si idCategoria=0 se muestran todos los disponibles,
        // si viene un id especifico se filtra por esa categoria
        List<Producto> productos;
        if (idCategoria == 0) {
            productos = productoRepository.findByDisponibleTrue();
        } else {
            productos = productoRepository.findByIdCategoriaAndDisponibleTrue(idCategoria);
        }
        model.addAttribute("productos",             productos);
        model.addAttribute("categoriaSeleccionada", idCategoria);
        model.addAttribute("seccion",               "menu");
        return "/menu/listado";
    }
}