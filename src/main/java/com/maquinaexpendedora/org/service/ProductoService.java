package com.maquinaexpendedora.org.service;

import com.maquinaexpendedora.org.domain.Producto;
import com.maquinaexpendedora.org.repository.ProductoRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.maquinaexpendedora.org.domain.Producto}.
 */
@Service
@Transactional
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Save a producto.
     *
     * @param producto the entity to save.
     * @return the persisted entity.
     */
    public Producto save(Producto producto) {
        log.debug("Request to save Producto : {}", producto);
        return productoRepository.save(producto);
    }

    /**
     * Update a producto.
     *
     * @param producto the entity to save.
     * @return the persisted entity.
     */
    public Producto update(Producto producto) {
        log.debug("Request to update Producto : {}", producto);
        return productoRepository.save(producto);
    }

    /**
     * Partially update a producto.
     *
     * @param producto the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Producto> partialUpdate(Producto producto) {
        log.debug("Request to partially update Producto : {}", producto);

        return productoRepository
            .findById(producto.getId())
            .map(existingProducto -> {
                if (producto.getClave() != null) {
                    existingProducto.setClave(producto.getClave());
                }
                if (producto.getNombreProducto() != null) {
                    existingProducto.setNombreProducto(producto.getNombreProducto());
                }
                if (producto.getPrecio() != null) {
                    existingProducto.setPrecio(producto.getPrecio());
                }

                return existingProducto;
            })
            .map(productoRepository::save);
    }

    /**
     * Get one producto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Producto> findOne(Long id) {
        log.debug("Request to get Producto : {}", id);
        return productoRepository.findById(id);
    }

    /**
     * Delete the producto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Producto : {}", id);
        productoRepository.deleteById(id);
    }
}
