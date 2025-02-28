package com.maquinaexpendedora.org.service.dto;

import java.io.Serializable;

public class ProductoPagadoDTO implements Serializable {

    private Double cantidadPagada;
    private Integer clave;
    private double cambio;

    public ProductoPagadoDTO() {}

    public ProductoPagadoDTO(Double cantidadPagada, Integer clave) {
        super();
        this.cantidadPagada = cantidadPagada;
        this.clave = clave;
    }

    public Double getCantidadPagada() {
        return cantidadPagada;
    }

    public void setCantidadPagada(Double cantidadPagada) {
        this.cantidadPagada = cantidadPagada;
    }

    public Integer getClave() {
        return clave;
    }

    public void setClave(Integer clave) {
        this.clave = clave;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }
}
