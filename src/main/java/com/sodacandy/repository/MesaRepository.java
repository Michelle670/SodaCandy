
package com.sodacandy.repository;

import com.sodacandy.domain.Mesa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer>
{
    public List<Mesa> findByActivoTrue();
}
