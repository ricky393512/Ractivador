package com.telcel.ractivador.ws;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.telcel.ractivador.exceptions.WebServiceConexionException;
import com.telcel.ractivador.net.Conexion;
import com.telcel.ractivador.net.Constantes;
import com.telcel.ractivador.pojo.RespuestaLogueo;
import com.telcel.ractivador.session.SessionManager;
import com.telcel.ractivador.views.ConsultaActivity;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by PIN7025 on 23/05/2017.
 */
public class LogueoWS extends AsyncTask<Void, Void, Boolean> {
private final String distribuidor;
private final String vendedor;
private RespuestaLogueo respuestaLogueo=null;
private ProgressDialog progreso;
private Context ctx;
private String mensajeFinal;
private String codigoeFinal;
    private Conexion conexion;
    private SessionManager session;
    public AsyncResponse delegate = null;
    private int id;


    public LogueoWS(String distribuidor, String vendedor, Context ctx,int id) {
        this.distribuidor = distribuidor;
        this.vendedor = vendedor;
        this.ctx=ctx;
        this.id=id;
        session = new SessionManager(ctx);
        }

@Override
protected void onPreExecute() {
        super.onPreExecute();
        progreso = new ProgressDialog(ctx);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Validando Crendenciales R7............");
        progreso.setCancelable(false);
        progreso.show();

        }

@Override
protected Boolean doInBackground(Void... params) {
        conexion = new Conexion(ctx);

        try {
        if (!conexion.isAvailableWSDL(Constantes.URL)) {
        Log.e("RVISOR MOBILE", "El WS " + Constantes.URL + " no esta en linea ");
        mensajeFinal= "Web Service: No Disponible";
        return false;
        }
        }catch(Exception e){
        mensajeFinal= e.getMessage();
        return false;
        }
        // Create the outgoing message
        respuestaLogueo = new RespuestaLogueo();

        SoapObject requestObject = new SoapObject(Constantes.NAMESPACE, Constantes.METHOD_NAME_LOGUEO);
        requestObject.addProperty("cod_distribuidor",distribuidor);
        requestObject.addProperty("cod_vendedor",vendedor);
        SoapSerializationEnvelope envelope = conexion.getSoapSerializationEnvelope(requestObject);
        HttpTransportSE ht = conexion.getHttpTransportSE(Constantes.URL,Constantes.TIME_OUT);
        try{

        Object retObj = conexion.llamadaAlWS(envelope,ht,Constantes.SOAP_ACTION_LOGUEO);
        respuestaLogueo = conexion.obtenerCredencialesSoap((SoapObject)retObj);
        }catch (WebServiceConexionException e){
        e.printStackTrace();
        respuestaLogueo.setCodigo(-1);
        respuestaLogueo.setMensaje(e.getMessage());
        }catch(Exception e){
        e.printStackTrace();
        respuestaLogueo.setCodigo(-1);
        respuestaLogueo.setMensaje(e.getMessage());
        }

        if(respuestaLogueo.getCodigo()==100)
        return true;
        else
        return false;
        }

@Override
protected void onPostExecute(final Boolean success) {
       // mAuthTask = null;
        progreso.dismiss();
        // showProgress(false);
        if (success) {
        Log.i("RVISOR MOBILE", "Entro a  guardar la session con el distribuidor: "+distribuidor+" y vendedor "+vendedor);
        session.createLoginSession(distribuidor, vendedor);
        // Store values at the time of the login attempt.
        Intent intent =               new Intent(ctx,ConsultaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(intent);
        //ctx.finish();

        } else {

            super.onPostExecute(false);
            //call delegate
            delegate.onProcessFinish(respuestaLogueo, id);


        }
        }

@Override
protected void onCancelled() {
        //mAuthTask = null;
        progreso.dismiss();
        // showProgress(false);
        }




}

