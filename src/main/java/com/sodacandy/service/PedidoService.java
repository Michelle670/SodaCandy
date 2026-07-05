
package com.sodacandy.service;
import com.sodacandy.domain.Pedido;
import com.sodacandy.repository.DetallePedidoRepository;
import com.sodacandy.repository.PedidoRepository;
import com.sodacandy.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Siempre se hace la anotacion @Service en la capa de servicio del MVC
@Service
public class PedidoService
{
    //Se enlazan los repositorios necesarios
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
    }

    //Devuelve todos los pedidos
    @Transactional(readOnly = true)
    public List<Pedido> getPedidos(){
        return pedidoRepository.findAll();
    }

    //CRUD: READ. Devuelve un solo pedido
    @Transactional(readOnly = true)
    public Optional<Pedido> getPedido(Integer idPedido){
        return pedidoRepository.findById(idPedido);
    }

    //CRUD: CREATE / UPDATE. Actualizar o crear un pedido
    @Transactional
    public void save(Pedido pedido){
        pedidoRepository.save(pedido);
    }

    //CRUD: DELETE. Elimina un pedido
    @Transactional
    public void delete(Integer idPedido){
        //Primero se verifica que el registro exista
        if (!pedidoRepository.existsById(idPedido)) {
            //El registro no se puede eliminar..porque no existe..
            throw new IllegalArgumentException("No se puede eliminar. No existe el pedido #" + idPedido);
        }
        try {
            pedidoRepository.deleteById(idPedido);
        } catch (DataIntegrityViolationException e) {
            //Se lanza una excepcion porque el pedido tiene datos asociados
            throw new IllegalStateException("No se elimina porque tiene datos asociados");
        }
    }
    
    // ==========================
    // FUNCIONES PARA HU
    // ==========================
    
    
}
