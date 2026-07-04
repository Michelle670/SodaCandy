//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.repository;
import com.sodacandy.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer>
{
    //Para el menu publico solo interesan las categorias activas
    public List<Categoria> findByActivoTrue();
}
