package com.sodacandy.service;

import com.sodacandy.domain.Producto;
import com.sodacandy.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

//Siempre se hace la anotacion @Service en la capa de servicio del MVC
@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(ProductoRepository productoRepository, FirebaseStorageService firebaseStorageService) {
        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
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
    public void save(Producto producto, MultipartFile imagen) {
        producto = productoRepository.save(producto);
        if (!imagen.isEmpty()) {
            try {
                String ruta = firebaseStorageService
                        .uploadImage(imagen, "producto", producto.getIdProducto());
                producto.setRutaImagen(ruta);
                productoRepository.save(producto);
            } catch (IOException e) {
            }
        }
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

    // Método de servicio que usa la consulta derivada
    @Transactional(readOnly = true)
    public List<Producto> consulta(Integer idCategoria) {
        if (idCategoria == null || idCategoria == 0) {
            return productoRepository.findAll(); // Si es "todos" (0), devuelve todo
        }
        return productoRepository.findByIdCategoriaAndDisponibleTrue(idCategoria);
    }
}
