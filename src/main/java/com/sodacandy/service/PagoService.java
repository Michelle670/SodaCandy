//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.service;
import com.sodacandy.domain.Pago;
import com.sodacandy.repository.PagoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//==============================================================================
// CURSO : APLICACIONES WEB Y PATRONES
// PROF: WILBERT MOLINA
// ENTREGABLE:PROYECTO
// GRUPO:6
// INTEGRANTES:RONALD CALDERON, SHEILYN CORDOBA, MICHELLE GUERRERO, SOFIA LOAIZA
//==============================================================================
/*
 * Servicio de Pago.
 * inyeccion por constructor, @Transactional(readOnly=true) en lecturas,
 * save() unico para CREATE/UPDATE y delete() con verificacion + manejo
 * de DataIntegrityViolationException.
 *
 * Nota: Pago NO lleva imagen, asi que NO se agrega FirebaseStorageService
 * (a diferencia de Categoria/Producto/Usuario).
 */
//==============================================================================
@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    //==========================================================
    // READ - Listado
    //==========================================================
    @Transactional(readOnly = true)
    public List<Pago> getPagos() {
        return pagoRepository.findAll();
    }

    //==========================================================
    // READ - Detalle por ID
    //==========================================================
    @Transactional(readOnly = true)
    public Optional<Pago> getPago(Integer idPago) {
        return pagoRepository.findById(idPago);
    }

    //==========================================================
    // READ - Por pedido (findBy del repositorio)
    // Hay un solo pago por pedido: sirve para saber si un pedido
    // ya fue cobrado antes de registrar un nuevo pago.
    //==========================================================
    @Transactional(readOnly = true)
    public Optional<Pago> getPagoPorPedido(Integer idPedido) {
        return pagoRepository.findByIdPedido(idPedido);
    }

    //==========================================================
    // CREATE / UPDATE
    // - Si idPago esta vacio: se inserta
    // - Si idPago NO esta vacio: se modifica
    //==========================================================
    @Transactional
    public void save(Pago pago) {
        pagoRepository.save(pago);
    }

    //==========================================================
    // DELETE - con manejo de excepciones 
    //==========================================================
    @Transactional
    public void delete(Integer idPago) {
        // Primero verificar que el registro existe
        if (!pagoRepository.existsById(idPago)) {
            // El registro no se puede eliminar porque no existe
            throw new IllegalArgumentException("No se puede eliminar el registro, no existe id " + idPago);
        }
        try {
            pagoRepository.deleteById(idPago);
        } catch (DataIntegrityViolationException e) {
            // El pago tiene datos asociados
            throw new IllegalStateException("No se elimina porque tiene datos asociados");
        }
    }
}