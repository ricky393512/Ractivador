package com.telcel.ractivador.pojo;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by PIN7025 on 28/12/2016.
 */
public class Activacion implements KvmSerializable {
    private String imei;
    private String iccid;
    private Integer codigoCiudad;
    private Integer idProducto;
    public Activacion() {
        super();
    }
    public String getImei() {
        return imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }
    public String getIccid() {
        return iccid;
    }
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
    public Integer getCodigoCiudad() {
        return codigoCiudad;
    }
    public void setCodigoCiudad(Integer codigoCiudad) {
        this.codigoCiudad = codigoCiudad;
    }
    private String codigoDistribuidor;
    private String codigoVendedor;
    private Integer idModalidad;

    public String getCodigoDistribuidor() {
        return codigoDistribuidor;
    }

    public void setCodigoDistribuidor(String codigoDistribuidor) {
        this.codigoDistribuidor = codigoDistribuidor;
    }

    public String getCodigoVendedor() {
        return codigoVendedor;
    }

    public void setCodigoVendedor(String codigoVendedor) {
        this.codigoVendedor = codigoVendedor;
    }

    public Integer getIdModalidad() {
        return idModalidad;
    }

    public void setIdModalidad(Integer idModalidad) {
        this.idModalidad = idModalidad;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public Object getProperty(int i) {
        switch(i)
        {
            case 0:
                return imei;
            case 1:
                return iccid;
            case 2:
                return codigoCiudad;
            case 3:
                return idProducto;
        }
        return null;

    }

    @Override
    public int getPropertyCount() {
        return 4;
    }

    @Override
    public void setProperty(int i, Object o) {

    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch(i)
        {
            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "imei";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "iccid";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "codigoCiudad";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "idProducto";
                break;
            default:break;
        }
    }

    @Override
    public String getInnerText() {
        return null;
    }

    @Override
    public void setInnerText(String s) {

    }
}
