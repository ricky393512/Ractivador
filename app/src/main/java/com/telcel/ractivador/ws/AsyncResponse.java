package com.telcel.ractivador.ws;

import com.telcel.ractivador.pojo.RespuestaLogueo;

/**
 * Created by PIN7025 on 23/05/2017.
 */
public interface AsyncResponse {
    void onProcessFinish(RespuestaLogueo respuestaLogueo, int id);
}
