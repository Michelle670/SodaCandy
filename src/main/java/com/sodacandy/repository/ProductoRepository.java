
package com.sodacandy.repository;

import com.sodacandy.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>
{
    public List<Producto> findByActivoTrue();
    // Todos los platillos disponibles para el menu publico
    public List<Producto> findByDisponibleTrue();
    //Platillos de una categoria que estan disponibles (menu publico)
    public List<Producto> findByIdCategoriaAndDisponibleTrue(Integer idCategoria);
}
