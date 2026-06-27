
package com.sodacandy.repository;

import com.sodacandy.domain.EstadoPedido;
import com.sodacandy.domain.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer>
{
    public List<Pedido> findByEstado(EstadoPedido estado);
    public List<Pedido> findByIdUsuario(Integer idUsuario);
}
