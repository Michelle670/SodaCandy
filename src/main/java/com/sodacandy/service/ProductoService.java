package com.sodacandy.service;

import com.sodacandy.domain.Producto;
import com.sodacandy.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Siempre se hace la anotacion @Service en la capa de servicio del MVC
@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Devuelve todos los productos o solo los activos
    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean soloActivos) {
        if (soloActivos) {
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }

    // READ
    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    // CREATE / UPDATE
    @Transactional
    public void save(Producto producto) {
        productoRepository.save(producto);
    }

    // HU-08: Marcar / desmarcar disponible (agotado)
    @Transactional
    public void toggleDisponible(Integer idProducto) {
        Producto p = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("No existe el producto id " + idProducto));
        p.setDisponible(!p.isDisponible());
        productoRepository.save(p);
    }

    // DELETE
    @Transactional
    public void delete(Integer idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException("No se puede eliminar el registro, no existe id " + idProducto);
        }
        try {
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se elimina porque tiene datos asociados.");
        }
    }
}
