package com.maquinaexpendedora.org.service;

import com.maquinaexpendedora.org.domain.Producto;
import com.maquinaexpendedora.org.repository.ProductoRepository;
import com.maquinaexpendedora.org.service.dto.ProductoPagadoDTO;
import com.maquinaexpendedora.org.web.rest.errors.CantidadPagadaNoAlcanzaException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CostCalculatorService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    public CostCalculatorService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public ProductoPagadoDTO getProductCost(ProductoPagadoDTO productoPagadoDto) {
        Optional<Producto> productoOp = productoRepository.findOneByClave(productoPagadoDto.getClave());
        Producto producto = productoOp
            .filter(prod -> prod.getPrecio() <= productoPagadoDto.getCantidadPagada())
            .orElseThrow(() -> new CantidadPagadaNoAlcanzaException());

        productoPagadoDto.setCambio(productoPagadoDto.getCantidadPagada() - producto.getPrecio());
        return productoPagadoDto;
    }
}
