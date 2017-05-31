package com.telcel.ractivador.ws;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.telcel.ractivador.exceptions.WebServiceConexionException;
import com.telcel.ractivador.net.Conexion;
import com.telcel.ractivador.net.Constantes;
import com.telcel.ractivador.pojo.Activacion;
import com.telcel.ractivador.pojo.RespuestaActivacion;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


/**
 * Created by PIN7025 on 27/12/2016.
 */
public class ActivacionWS extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private Activacion activacion;
    private int id;
    private Conexion conexion;
    private ProgressDialog progreso;
    private RespuestaActivacion respuestaActivacion = new RespuestaActivacion();
    public AsyncResponseActivacion delegate = null;



    public ActivacionWS(Context context, Activacion activacion, int id){
        this.context=context;
        this.activacion = activacion;
        this.id=id;
        this.conexion= new Conexion(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        super.onPreExecute();
        progreso = new ProgressDialog(context);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Activando ............");
        progreso.setCancelable(false);
        progreso.show();

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (!conexion.isAvailableWSDL(Constantes.URL)) {
                Log.e("RVISOR MOBILE", "El WS " + Constantes.URL + " no esta en linea ");
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        // Create the outgoing message
        SoapObject requestObject = new SoapObject(Constantes.NAMESPACE, Constantes.METHOD_NAME_ACTIVA);
        // Set Parameter
        requestObject.addProperty("imei", activacion.getImei());
        requestObject.addProperty("iccid", activacion.getIccid());
        requestObject.addProperty("cod_ciudad", activacion.getCodigoCiudad());
        requestObject.addProperty("cod_distribuidor", activacion.getCodigoDistribuidor());
        requestObject.addProperty("cod_vendedor", activacion.getCodigoVendedor());
        requestObject.addProperty("id_tipo_producto", activacion.getTipoProducto().getIdProducto());
        requestObject.addProperty("id_modalidad_activacion", activacion.getTipoProducto().getIdModalidad());


        SoapSerializationEnvelope envelope = conexion.getSoapSerializationEnvelope(requestObject);
        HttpTransportSE ht = conexion.getHttpTransportSE(Constantes.URL, Constantes.TIME_OUT);

        try {

            Object retObj = conexion.llamadaAlWS(envelope, ht, Constantes.SOAP_ACTION_ACTIVA);
            respuestaActivacion = conexion.obtenerRespuestaActivaSoap((SoapObject) retObj);
        } catch (WebServiceConexionException e) {
            e.printStackTrace();
            respuestaActivacion.setCodigo(-1);
            respuestaActivacion.setMensaje(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            respuestaActivacion.setCodigo(-1);
            respuestaActivacion.setMensaje(e.getMessage());
        }

        if (respuestaActivacion.getCodigo() == 100)
            return true;
        else
            return false;
    }


    @Override
    protected void onPostExecute(final Boolean success) {

        progreso.dismiss();
        // showProgress(false);
        //if (success) {
            delegate.onProcessFinish(respuestaActivacion, id);
        //}


    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
    }





}



