package com.sodacandy.repository;
 
import com.sodacandy.domain.Pago;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer>
{
    //Como hay un solo pago por pedido, se recupera por el id del pedido
    public Optional<Pago> findByIdPedido(Integer idPedido);
 
    // HU-20: pagos hechos dentro de un rango de fechas (para el reporte del dia).
    // Spring genera la consulta sola por el nombre del metodo (findByFechaBetween).
    public List<Pago> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
 