//==============================================================================
// IMPORTES
//==============================================================================
package com.sodacandy.service;
import com.sodacandy.domain.EstadoMesa;
import com.sodacandy.domain.Mesa;
import com.sodacandy.repository.MesaRepository;
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

//Siempre se hace la anotacion @Service en la capa de servicio del MVC
@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    // Devuelve todas las mesas o solo las activas
    @Transactional(readOnly = true)
    public List<Mesa> getMesas(boolean soloActivas) {
        if (soloActivas) {
            return mesaRepository.findByActivoTrue();
        }
        return mesaRepository.findAll();
    }

    // READ
    @Transactional(readOnly = true)
    public Optional<Mesa> getMesa(Integer idMesa) {
        return mesaRepository.findById(idMesa);
    }

    // CREATE / UPDATE
    @Transactional
    public void save(Mesa mesa) {
        mesaRepository.save(mesa);
    }

    // HU-11: abrir una mesa libre -> pasa a OCUPADA
    @Transactional
    public void abrir(Integer idMesa) {
        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new IllegalArgumentException("No existe la mesa id " + idMesa));
        if (mesa.getEstado() != EstadoMesa.LIBRE) {
            throw new IllegalStateException("La mesa no esta libre, no se puede abrir.");
        }
        mesa.setEstado(EstadoMesa.OCUPADA);
        mesaRepository.save(mesa);
    }

    // Liberar una mesa -> vuelve a LIBRE
    @Transactional
    public void liberar(Integer idMesa) {
        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new IllegalArgumentException("No existe la mesa id " + idMesa));
        mesa.setEstado(EstadoMesa.LIBRE);
        mesaRepository.save(mesa);
    }

    // DELETE
    @Transactional
    public void delete(Integer idMesa) {
        if (!mesaRepository.existsById(idMesa)) {
            throw new IllegalArgumentException("No se puede eliminar el registro, no existe id " + idMesa);
        }
        try {
            mesaRepository.deleteById(idMesa);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se elimina porque tiene datos asociados.");
        }
    }
}
