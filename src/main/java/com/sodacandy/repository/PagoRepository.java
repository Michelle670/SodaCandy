
package com.sodacandy.repository;

import com.sodacandy.domain.Pago;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer>
{
    //Como hay un solo pago por pedido, se recupera por el id del pedido
    public Optional<Pago> findByIdPedido(Integer idPedido);
}
