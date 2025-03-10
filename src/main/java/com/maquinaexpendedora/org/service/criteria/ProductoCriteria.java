package com.maquinaexpendedora.org.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.maquinaexpendedora.org.domain.Producto} entity. This class is used
 * in {@link com.maquinaexpendedora.org.web.rest.ProductoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /productos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter clave;

    private StringFilter nombreProducto;

    private DoubleFilter precio;

    private Boolean distinct;

    public ProductoCriteria() {}

    public ProductoCriteria(ProductoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.clave = other.optionalClave().map(IntegerFilter::copy).orElse(null);
        this.nombreProducto = other.optionalNombreProducto().map(StringFilter::copy).orElse(null);
        this.precio = other.optionalPrecio().map(DoubleFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProductoCriteria copy() {
        return new ProductoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getClave() {
        return clave;
    }

    public Optional<IntegerFilter> optionalClave() {
        return Optional.ofNullable(clave);
    }

    public IntegerFilter clave() {
        if (clave == null) {
            setClave(new IntegerFilter());
        }
        return clave;
    }

    public void setClave(IntegerFilter clave) {
        this.clave = clave;
    }

    public StringFilter getNombreProducto() {
        return nombreProducto;
    }

    public Optional<StringFilter> optionalNombreProducto() {
        return Optional.ofNullable(nombreProducto);
    }

    public StringFilter nombreProducto() {
        if (nombreProducto == null) {
            setNombreProducto(new StringFilter());
        }
        return nombreProducto;
    }

    public void setNombreProducto(StringFilter nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public DoubleFilter getPrecio() {
        return precio;
    }

    public Optional<DoubleFilter> optionalPrecio() {
        return Optional.ofNullable(precio);
    }

    public DoubleFilter precio() {
        if (precio == null) {
            setPrecio(new DoubleFilter());
        }
        return precio;
    }

    public void setPrecio(DoubleFilter precio) {
        this.precio = precio;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductoCriteria that = (ProductoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(clave, that.clave) &&
            Objects.equals(nombreProducto, that.nombreProducto) &&
            Objects.equals(precio, that.precio) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clave, nombreProducto, precio, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalClave().map(f -> "clave=" + f + ", ").orElse("") +
            optionalNombreProducto().map(f -> "nombreProducto=" + f + ", ").orElse("") +
            optionalPrecio().map(f -> "precio=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
