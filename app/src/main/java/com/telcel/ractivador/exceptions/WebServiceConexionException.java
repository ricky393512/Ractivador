package com.telcel.ractivador.exceptions;

/**
 * Created by PIN7025 on 18/01/2017.
 */
public class WebServiceConexionException extends RuntimeException {

    public WebServiceConexionException(final String mensaje){
        super(mensaje);
    }

    public WebServiceConexionException(String mensaje, Throwable excepcion){
        super(mensaje,excepcion);
    }


}
