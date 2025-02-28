package com.maquinaexpendedora.org.repository;

import com.maquinaexpendedora.org.domain.Producto;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Producto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    public Optional<Producto> findOneByClave(Integer clave);
}
