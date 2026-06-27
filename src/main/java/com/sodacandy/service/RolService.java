
package com.sodacandy.service;

import com.sodacandy.domain.Rol;
import com.sodacandy.repository.RolRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Siempre se hace la anotacion @Service en la capa de servicio del MVC
@Service
public class RolService
{
    //se enlaza el repositorio de rol por constructor
    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository)
    {
        this.rolRepository = rolRepository;
    }

    //Devuelve todos los roles
    @Transactional(readOnly = true)
    public List<Rol> getRoles()
    {
        return rolRepository.findAll();
    }

    //CRUD: CREATE..READ..UPDATE..DELETE

    //READ
    @Transactional(readOnly = true)
    public Optional<Rol> getRol(Integer idRol)
    {
        return rolRepository.findById(idRol);
    }

    //CREATE / UPDATE
    //Si el idRol viene vacio..se inserta
    //Si el idRol NO viene vacio..se modifica
    @Transactional
    public void save(Rol rol)
    {
        rolRepository.save(rol);
    }

    //DELETE
    @Transactional
    public void delete(Integer idRol)
    {
        //Primero se verifica que el registro exista
        if (!rolRepository.existsById(idRol)) {
            //El registro no se puede eliminar..porque no existe..
            throw new IllegalArgumentException("No se puede eliminar el registro, no existe id " + idRol);
        }
        try {
            rolRepository.deleteById(idRol);
        } catch (DataIntegrityViolationException e) {
            //Se lanza una excepcion porque el rol tiene usuarios asociados
            throw new IllegalStateException("No se elimina porque tiene datos asociados");
        }
    }
}
