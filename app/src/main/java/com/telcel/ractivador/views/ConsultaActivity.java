package com.telcel.ractivador.views;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Select;
import com.telcel.ractivador.R;
import com.telcel.ractivador.exceptions.WebServiceConexionException;
import com.telcel.ractivador.net.Conexion;
import com.telcel.ractivador.net.Constantes;
import com.telcel.ractivador.net.Mensaje;
import com.telcel.ractivador.pojo.Activacion;
import com.telcel.ractivador.pojo.Credencial;
import com.telcel.ractivador.pojo.RespuestaActivacion;
import com.telcel.ractivador.pojo.TipoProducto;
import com.telcel.ractivador.session.SessionManager;
import com.telcel.ractivador.ws.CoopelWS;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConsultaActivity extends BaseAppActivity {
    @Order(value=2)
    @NotEmpty(message = "Debes ingresar un imei")
    @Length(max=15,min = 15,message = "Debes ingresar un imei de 15 digitos")
    EditText campo_imei;
    @Order(value=1)
    @NotEmpty(message = "Debes ingresar un iccid")
    @Length(max=19,min = 19,message = "Debes ingresar un iccid de 19 digitos")
    EditText campo_iccid;
    @Order(value=3)
    @NotEmpty(message = "Debes ingresar un codigo de ciudad")
    @Length(max=2,min = 2,message = "Debes ingresar un codigo ciudad de 2 digitos")
    EditText campo_codigo_ciudad;
    TextView txtClaveDistribuidor;
    TextView txtClaveVendedor;
    TextView txtResultado;
    @Select(message = "Debes seleccionar un producto")
    Spinner mySpinner;
    CoopelWS coopelWS = null;
    // Session Manager Class
    SessionManager session;
    private NotificationManager notifyMgr;
    Credencial credencial= new Credencial();
    private Conexion conexion;
    private Mensaje mensaje;
    private final String LOG_TAG="RVISOR";


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void confirmaAcciones(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ConsultaActivity.this);
/*
        View child = getLayoutInflater().inflate(R.layout.midialogo, null);
        builder.setView(child);

        builder.setMessage("¿Estas seguro de mandar la activación?")
                .setTitle("Confirmacion")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()  {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "Confirmacion Aceptada.");
                        dialog.dismiss();
                        realizaActivacion();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "Confirmacion Cancelada.");
                        dialog.dismiss();
                    }
                });
*/
        builder.create();
        builder.show();

    }



    public void  realizaActivacion(){
        final TipoProducto tipoProducto = (TipoProducto) ((Spinner) findViewById(R.id.my_spinner)).getSelectedItem();
        /*   Log.i("RVISOR MOBILE","VAlor de producto seleccionado !!!"+tipoProducto.getIdProducto());
        if(tipoProducto.getIdProducto()==-1){
            Log.e("RVISOR MOBILE","VAlor -1 de producto seleccionado !!!");
            mensaje.getMostrarAlerta(ConsultaActivity.this,"Error !!!","Se ha presentado el siguiente problema con el WS de Catalogos \n"+
                    "Favor de recargar el catalogo para que puedas elegir un producto valido","REGRESAR A LA ACTIVACION"
            );
            return;
        }
*/
        validate();
        if (validated) {
            final Activacion activacion = new Activacion();
            activacion.setImei(campo_imei.getText().toString());
            activacion.setIccid(campo_iccid.getText().toString());
            try {
                activacion.setCodigoCiudad(Integer.parseInt(campo_codigo_ciudad.getText().toString()));
            } catch (NumberFormatException e) {
                campo_codigo_ciudad.setError("Debes ingresar un codigo de ciudad");
                campo_codigo_ciudad.requestFocus();
            }
            activacion.setIdProducto(tipoProducto.getIdProducto());
            activacion.setIdModalidad(tipoProducto.getIdModalidad());
            activacion.setCodigoDistribuidor(credencial.getClaveDistribuidor());
            activacion.setCodigoVendedor(credencial.getClaveVendedor());

            new AsyncTask<Void, Void, Boolean>() {
                private TextView txtResultado;
                private List<TipoProducto> listaProductos;

                private ProgressDialog progreso;
                RespuestaActivacion respuestaActivacion = new RespuestaActivacion();


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progreso = new ProgressDialog(ConsultaActivity.this);
                    progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progreso.setMessage("Activando ............");
                    progreso.setCancelable(false);
                    progreso.show();

                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    listaProductos = new ArrayList<TipoProducto>();
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
                    requestObject.addProperty("id_tipo_producto", tipoProducto.getIdProducto());
                    requestObject.addProperty("id_modalidad_activacion", tipoProducto.getIdModalidad());


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

                    if (!success) {
                        String titulo = "Error !!!";
                        String mensaje1 = "Se ha presentado el siguiente problema: \n"
                                + respuestaActivacion.getMensaje() +
                                " con codigo " + respuestaActivacion.getCodigo();
                        mensaje.getMostrarAlerta(ConsultaActivity.this, titulo,
                                mensaje1, "OK");


                        mensaje.getNotificationError(1, R.mipmap.ic_launcher, "Error de Activacion", "Se ha presentado el siguiente problema con la activacion: \n"
                                + respuestaActivacion.getMensaje()
                                + "\n"
                        );


                    } else {

                        mostrarAlertaExito(ConsultaActivity.this, "Atención", "Se ha realizado la activacion correctamente \n"
                                + respuestaActivacion.getMensaje()
                                + "\n"
                                + "Con telefono: "

                                + respuestaActivacion.getTelefono()
                                + "\n"
                                + "Y monto: "

                                + respuestaActivacion.getMonto()
                                + "\n", "NUEVA ACTIVACION"
                        );
                        mensaje.getNotificationExito(1, R.mipmap.ic_launcher, "Aviso de Activacion", "Se ha realizado la activacion correctamente \n"
                                + respuestaActivacion.getMensaje()
                                + "\n"
                                + "Con telefono: "

                                + respuestaActivacion.getTelefono()
                                + "\n"
                                + "Y monto: "

                                + respuestaActivacion.getMonto()
                                + "\n", respuestaActivacion.getTelefono(), respuestaActivacion.getMonto());
                    }


                }

                @Override
                protected void onCancelled() {
                    Toast.makeText(ConsultaActivity.this, "Error", Toast.LENGTH_LONG).show();
                }


            }.execute();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

   public void initView(){
       Button btnIccid = (Button) findViewById(R.id.btnIccid);
       Button btnImei = (Button) findViewById(R.id.btnImei);
       Button btnActivar = (Button) findViewById(R.id.boton_aceptar);
       Button btnNueva = (Button) findViewById(R.id.btnNueva);
       Button btnProducto = (Button) findViewById(R.id.btnProducto);
       Button btnConsultar = (Button)findViewById(R.id.btnConsultar);

       mySpinner = (Spinner) findViewById(R.id.my_spinner);
       campo_imei = (EditText) findViewById(R.id.campo_imei);
       campo_iccid = (EditText) findViewById(R.id.campo_iccid);
       campo_codigo_ciudad = (EditText) findViewById(R.id.campo_ciudad);
       txtResultado = (TextView) findViewById(R.id.txtResultado);
       HashMap<String,String> mapaCrendenciales= session.getUserDetails();
       credencial.setClaveDistribuidor(mapaCrendenciales.get("distribuidor"));
       credencial.setClaveVendedor(mapaCrendenciales.get("vendedor"));
       txtClaveVendedor = (TextView) findViewById(R.id.txtClaveVendedor);
       txtClaveDistribuidor = (TextView) findViewById(R.id.txtClaveDistribuidor);
       txtClaveVendedor.setText(credencial.getClaveVendedor());
       txtClaveDistribuidor.setText(credencial.getClaveDistribuidor());
       mensaje=new Mensaje(getApplicationContext());


       btnConsultar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               System.out.println("Entroooooooo!");

           }
       });

       btnProducto.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               cargaCatalogoProductos();
           }
       });

       btnNueva.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               limpiaPantalla();
           }
       });


       btnActivar.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               // And to get the actual User object that was selected, you can do this.
               //TipoProducto tipoProducto = (TipoProducto) ((Spinner) findViewById(R.id.my_spinner)).getSelectedItem();
               realizaActivacion();
          /*     if(conexion.estaConectado()){
                   confirmaAcciones();
               }else{

                   mensaje.getMostrarAlerta(ConsultaActivity.this, getString(R.string.error_titulo_conexion_nodisponible),
                           getString(R.string.error_conexion_nodisponible),"OK");
               }
 */

           }
       });

       btnImei.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               ScanEAN();
           }
       });


       btnIccid.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               ScanIccid();
           }
       });



   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        session = new SessionManager(getApplicationContext());
        conexion = new Conexion(getApplicationContext());
        session.firstRun();
        initView();
        cargaCatalogoProductos();
    }




    private void cargaCatalogoProductos(){
        conexion.updateAndroidSecurityProvider(this);
        coopelWS = new CoopelWS(getApplicationContext(), mySpinner,credencial.getClaveDistribuidor());
        coopelWS.execute();

    }




    private void ScanEAN() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        this.startActivityForResult(intent, 1);
    }


    private void ScanIccid() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        this.startActivityForResult(intent, 2);
    }


    private void limpiaPantalla() {
        campo_imei.setText(null);
        campo_iccid.setText(null);
        campo_codigo_ciudad.setText(null);
        txtResultado.setText(null);
        cargaCatalogoProductos();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1)
                campo_imei.setText(data.getStringExtra("SCAN_RESULT"));
            else if (requestCode == 2)
                campo_iccid.setText(data.getStringExtra("SCAN_RESULT"));

        }
    }





    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }




    public void mostrarAlertaExito(Context context, String title, String message,String titlePositiveButton) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ConsultaActivity.this, R.style.myDialog);
        alert.setTitle(title);
        alert.setMessage(message+ "\n"
        );
        alert.setPositiveButton(titlePositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                dialog.dismiss();
                finish();
                startActivity(getIntent());

            }
        });
        AlertDialog dialog = alert.create();
        dialog.setCancelable(false);
        dialog.show();
    }


}

