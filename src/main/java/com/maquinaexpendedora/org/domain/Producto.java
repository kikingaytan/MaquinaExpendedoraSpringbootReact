package com.maquinaexpendedora.org.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A Producto.
 */
@Entity
@Table(name = "producto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "clave")
    private Integer clave;

    @Column(name = "nombre_producto")
    private String nombreProducto;

    @Column(name = "precio")
    private Double precio;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Producto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getClave() {
        return this.clave;
    }

    public Producto clave(Integer clave) {
        this.setClave(clave);
        return this;
    }

    public void setClave(Integer clave) {
        this.clave = clave;
    }

    public String getNombreProducto() {
        return this.nombreProducto;
    }

    public Producto nombreProducto(String nombreProducto) {
        this.setNombreProducto(nombreProducto);
        return this;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Double getPrecio() {
        return this.precio;
    }

    public Producto precio(Double precio) {
        this.setPrecio(precio);
        return this;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto)) {
            return false;
        }
        return getId() != null && getId().equals(((Producto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Producto{" +
            "id=" + getId() +
            ", clave=" + getClave() +
            ", nombreProducto='" + getNombreProducto() + "'" +
            ", precio=" + getPrecio() +
            "}";
    }
}
