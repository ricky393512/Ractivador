package com.telcel.ractivador.pojo;

import java.io.Serializable;

/**
 * Created by PIN7025 on 26/12/2016.
 */
public class Credencial implements Serializable {

    private String claveVendedor;
    private String claveDistribuidor;

    public Credencial() {
    }

    public String getClaveVendedor() {
        return claveVendedor;
    }

    public void setClaveVendedor(String claveVendedor) {
        this.claveVendedor = claveVendedor;
    }

    public String getClaveDistribuidor() {
        return claveDistribuidor;
    }

    public void setClaveDistribuidor(String claveDistribuidor) {
        this.claveDistribuidor = claveDistribuidor;
    }
}
