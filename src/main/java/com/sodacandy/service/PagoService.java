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
import com.sodacandy.domain.LineaReporte;
import com.sodacandy.domain.MetodoPago;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
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
    // REPORTE DE VENTAS (HU-20)
    // Agrupa los pagos por metodo y calcula total y cantidad.
    // Reutiliza getPagos(); no requiere consulta nueva.
    //==========================================================

    @Transactional(readOnly = true)
    public List<LineaReporte> getReporteVentas() {
        // metodo -> [cantidad, total]
        Map<MetodoPago, LineaReporte> mapa = new LinkedHashMap<>();
        // Inicializa todos los metodos en cero (asi salen aunque no tengan ventas)
        for (MetodoPago m : MetodoPago.values()) {
            mapa.put(m, new LineaReporte(m.name(), 0, BigDecimal.ZERO));
        }
        // Suma cada pago a su metodo
        for (Pago pago : pagoRepository.findAll()) {
            LineaReporte linea = mapa.get(pago.getMetodo());
            linea.setCantidad(linea.getCantidad() + 1);
            linea.setTotal(linea.getTotal().add(pago.getMonto()));
        }
        return new ArrayList<>(mapa.values());
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