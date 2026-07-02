package com.sodacandy.service;

import com.sodacandy.domain.Categoria;
import com.sodacandy.repository.CategoriaRepository;
import com.sodacandy.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Siempre se hace la anotacion @Service en la capa de servicio del MVC
@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    // Devuelve todas las categorias o solo las activas
    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean soloActivas) {
        if (soloActivas) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }

    // READ
    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    // CREATE / UPDATE
    @Transactional
    public void save(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    // DELETE:No se permite si tiene productos asociados 
    @Transactional
    public void delete(Integer idCategoria) {
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException("No se puede eliminar el registro, no existe id " + idCategoria);
        }
        // Verificar si hay productos ligados a esta categoría
        if (!productoRepository.findByIdCategoriaAndDisponibleTrue(idCategoria).isEmpty()
                || productoRepository.findAll().stream()
                        .anyMatch(p -> idCategoria.equals(p.getIdCategoria()))) {
            throw new IllegalStateException("No se puede eliminar porque tiene productos asociados.");
        }
        try {
            categoriaRepository.deleteById(idCategoria);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se elimina porque tiene datos asociados.");
        }
    }
}
