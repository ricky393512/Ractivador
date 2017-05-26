package com.telcel.ractivador.pojo;

/**
 * Created by PIN7025 on 26/12/2016.
 */
public class TipoProducto {

    private String descripcion;
    private int idModalidad;
    private int idProducto;


    public TipoProducto() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdModalidad() {
        return idModalidad;
    }

    public void setIdModalidad(int idModalidad) {
        this.idModalidad = idModalidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public String toString() {
        return this.descripcion;            // What to display in the Spinner list.
    }
}
