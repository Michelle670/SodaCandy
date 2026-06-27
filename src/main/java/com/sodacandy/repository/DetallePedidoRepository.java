
package com.sodacandy.repository;

import com.sodacandy.domain.DetallePedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer>
{
    public List<DetallePedido> findByIdPedido(Integer idPedido);
}
