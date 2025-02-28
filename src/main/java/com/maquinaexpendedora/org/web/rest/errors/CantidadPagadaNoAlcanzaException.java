package com.maquinaexpendedora.org.web.rest.errors;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class CantidadPagadaNoAlcanzaException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public CantidadPagadaNoAlcanzaException() {
        super(ErrorConstants.CANTIDAD_PAGADA_NO_ALCANZA_TYPE, "No Te alcanza Pobreton!", "userManagement", "emailexists");
    }
}
