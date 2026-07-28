package com.sodacandy.service;
import com.sodacandy.domain.DetallePedido;
import com.sodacandy.repository.DetallePedidoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Siempre se hace la anotacion @Service en la capa de servicio del MVC
@Service
public class DetallePedidoService
{
    //Se enlazan los repositorios necesarios
    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedidoService(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Transactional
    public void save(DetallePedido detallePedido) {
        detallePedidoRepository.save(detallePedido);
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> getDetallesPorPedido(Integer idPedido) {
        return detallePedidoRepository.findByIdPedido(idPedido);
    }
}
