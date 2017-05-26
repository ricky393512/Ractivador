package com.telcel.ractivador.net;

/**
 * Created by PIN7025 on 19/01/2017.
 */
public class Constantes {

    public final static String URL ="https://www.r7.telcel.com/wscadenas/wsActivaMobile?wsdl";
    public final static String NAMESPACE = "http://ws.telcel.com/";;
    public final static int TIME_OUT=70000;

    public final static String METHOD_NAME_LOGUEO = "realiza_autenticacion";
    public final static String SOAP_ACTION_LOGUEO = "\"http://ws.telcel.com/realiza_autenticacion\"";
    public final static String METHOD_NAME_LISTADO_PRODUCTOS_ACTIVACION = "listado_productos";
    public final static String SOAP_ACTION_LISTADO_PRODUCTOS_ACTIVACION = "\"http://ws.telcel.com/listado_productos\"";
    public final static String METHOD_NAME_ACTIVA = "realiza_activacion";
    public final static String SOAP_ACTION_ACTIVA = "\"http://ws.telcel.com/realiza_activacion\"";


}
