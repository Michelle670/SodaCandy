
package com.sodacandy.service;

import com.sodacandy.domain.Pedido;
import com.sodacandy.repository.PedidoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Siempre se hace la anotacion @Service en la capa de servicio del MVC
@Service
public class PedidoService
{
    //se enlaza el repositorio de rol por constructor
    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository)
    {
        this.pedidoRepository = pedidoRepository;
    }

    //Devuelve todos los roles
    @Transactional(readOnly = true)
    public List<Pedido> getPedidos()
    {
        return pedidoRepository.findAll();
    }

    //CRUD: CREATE..READ..UPDATE..DELETE

    //READ
    @Transactional(readOnly = true)
    public Optional<Pedido> getPedido(Integer idPedido)
    {
        return pedidoRepository.findById(idPedido);
    }

    //CREATE / UPDATE
    @Transactional
    public void save(Pedido pedido)
    {
        pedidoRepository.save(pedido);
    }

    //DELETE
    @Transactional
    public void delete(Integer idPedido)
    {
        //Primero se verifica que el registro exista
        if (!pedidoRepository.existsById(idPedido)) {
            //El registro no se puede eliminar..porque no existe..
            throw new IllegalArgumentException("No se puede eliminar el registro, no existe id " + idPedido);
        }
        try {
            pedidoRepository.deleteById(idPedido);
        } catch (DataIntegrityViolationException e) {
            //Se lanza una excepcion porque el rol tiene usuarios asociados
            throw new IllegalStateException("No se elimina porque tiene datos asociados");
        }
    }
}
