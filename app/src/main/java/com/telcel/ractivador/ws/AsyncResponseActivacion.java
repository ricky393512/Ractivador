package com.telcel.ractivador.ws;

import com.telcel.ractivador.pojo.RespuestaActivacion;

/**
 * Created by PIN7025 on 23/05/2017.
 */
public interface AsyncResponseActivacion {
    void onProcessFinish(RespuestaActivacion respuestaActivacion, int id);
}
