package com.telcel.ractivador.ws;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.telcel.ractivador.R;
import com.telcel.ractivador.adapters.NothingSelectedSpinnerAdapter;
import com.telcel.ractivador.exceptions.WebServiceConexionException;
import com.telcel.ractivador.net.Conexion;
import com.telcel.ractivador.net.Constantes;
import com.telcel.ractivador.pojo.TipoProducto;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by PIN7025 on 27/12/2016.
 */
public class CoopelWS extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private List<TipoProducto> listaProductos;
    private Spinner mySpinner;
    private String codigoDistribuidor;
    private Conexion conexion;

    public CoopelWS(Context context, Spinner mySpinner, String codigoDistribuidor){
        this.context=context;
        this.mySpinner = mySpinner;
        this.codigoDistribuidor=codigoDistribuidor;
        this.conexion= new Conexion(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        listaProductos = new ArrayList<>();
        try {
            if (!conexion.isAvailableWSDL(Constantes.URL)) {
                Log.e("RVISOR MOBILE", "El WS " + Constantes.URL + " no esta en linea ");
                return false;
            }
        }catch(Exception e){
            return false;
        }
        // Create the outgoing message
        SoapObject requestObject = new SoapObject(Constantes.NAMESPACE, Constantes.METHOD_NAME_LISTADO_PRODUCTOS_ACTIVACION);
        // Set Parameter
        requestObject.addProperty("cod_distribuidor",codigoDistribuidor);
        // Create soap envelop .use version 1.1 of soap
        SoapSerializationEnvelope envelope = conexion.getSoapSerializationEnvelope(requestObject);
        HttpTransportSE ht = conexion.getHttpTransportSE(Constantes.URL,Constantes.TIME_OUT);
        try{
            Object retObj = conexion.llamadaAlWS(envelope,ht,Constantes.SOAP_ACTION_LISTADO_PRODUCTOS_ACTIVACION);
            listaProductos = conexion.obtenerCatalogoProductoActivacionFromSoap((SoapObject)retObj);
        }catch (WebServiceConexionException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    protected void onPostExecute(final Boolean success) {
        if(!success){
    try{

        if (!conexion.isAvailableWSDL(Constantes.URL)) {
                Log.e("RVISOR MOBILE", "El WS " + Constantes.URL + " no esta en linea ");

            }

        }catch(Exception e){
        Log.e("RVISOR MOBILE", "El WS " + Constantes.URL + " no esta en linea por "+e.getMessage());


        }

            List<TipoProducto> listTP = new ArrayList<>();
          TipoProducto tp1 = new TipoProducto();
        tp1.setIdProducto(-1);
        tp1.setDescripcion("NO DISPONIBLE WEB SERVICES  OPRIMIR BOTON PRODUCTOS PARA RECARGAR CATALOGO DE PRODUCTOS");
        listTP.add(tp1);
            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.row,  listTP);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            mySpinner.setAdapter(adapter);
        }
        else{
            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.row, listaProductos);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            mySpinner.setAdapter(
                    new NothingSelectedSpinnerAdapter(
                            adapter,
                            R.layout.contact_spinner_row_nothing_selected,
                            // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                            context));
            Toast.makeText(context, "Catalogos Actualizados",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
    }





}



