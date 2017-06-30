package com.telcel.ractivador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.telcel.ractivador.net.Conexion;
import com.telcel.ractivador.pojo.RespuestaLogueo;
import com.telcel.ractivador.session.SessionManager;
import com.telcel.ractivador.utils.Autoupdater;
import com.telcel.ractivador.views.BaseAppActivity;
import com.telcel.ractivador.views.ConsultaActivity;
import com.telcel.ractivador.ws.AsyncResponse;
import com.telcel.ractivador.ws.LogueoWS;

public class MainActivity extends BaseAppActivity implements AsyncResponse{

    // UI references.
    @Order(value=1)
    @NotEmpty(message = "Debes ingresar la Clave Distribuidor")
    @Length(max=5,min = 5,message = "La clave de distribuidor tener 5 digitos")
    private EditText mClaveDistribuidorView;
    @Order(value=2)
    @NotEmpty(message = "Debes  ingresar Clave Vendedor")
    @Length(max=5,min = 5,message = "La clave de vendedor tener 5 digitos")
    private EditText mClaveVendedorView;
    private Button mSignInButton;
    private String distribuidor;
    private String vendedor;
    private Conexion conexion;
    private boolean firstRun;
    private LogueoWS logueoWS;
    // Session Manager Class
    private SessionManager session;
    private static final int ASYNC_ONE = 1; //ID for async
    private Autoupdater updater;
    //private RelativeLayout loadingPanel;



    private void initView() {
        mClaveDistribuidorView = (EditText) findViewById(R.id.distribuidor);
        mClaveVendedorView = (EditText) findViewById(R.id.vendedor);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });
        conexion = new Conexion(this);
        conexion.updateAndroidSecurityProvider(this);


        mClaveDistribuidorView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(mClaveDistribuidorView.getText().length()<5 || mClaveDistribuidorView.getText().length()>5){
                    mClaveDistribuidorView.setTextColor(Color.BLACK);
                }else{
                    mClaveDistribuidorView.setTextColor(Color.BLUE);
                }

            }
        });

        mClaveVendedorView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(mClaveVendedorView.getText().length()<5 || mClaveVendedorView.getText().length()>5 ){
                    mClaveVendedorView.setTextColor(Color.BLACK);
                }else{
                    mClaveVendedorView.setTextColor(Color.BLUE);
                }

            }
        });

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checaActualizaciones();
        subscribeToPushService();
        session = new SessionManager(getApplicationContext());
        firstRun = session.isFirstRun();
        if (!firstRun) {
            Intent intent = new Intent(this, ConsultaActivity.class);
            startActivity(intent);
            Log.i("RVISOR MOBILE", "Primera vez que se ejecuta---firstRun(false): " + Boolean.valueOf(firstRun).toString());
        } else {
            Log.e("RVISOR MOBILE", "No es la Primera vez que se ejecuta---firstRun(true): " + Boolean.valueOf(firstRun).toString());
            initView();

        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        Log.e("Ractivador","Entreeeeeeeeeeee!!");
        Log.e("Ractivador","Estoy conectado !!"+conexion.estaConectado());
        if(!conexion.estaConectado()) {
            mostrarAlerta("NO ESTA CONECTADO","RACTIVADOR");
            return;
        }
        validate();
        if(validated){
            // Our form is successfully validated, so, do your stuffs here...
            distribuidor = mClaveDistribuidorView.getText().toString();
            vendedor = mClaveVendedorView.getText().toString();
            logueoWS = new LogueoWS(distribuidor, vendedor,MainActivity.this, ASYNC_ONE);
            logueoWS.delegate=this;
            logueoWS.execute((Void) null);
        }

    }


    @Override
    public void onProcessFinish(RespuestaLogueo respuestaLogueo, int id) {
        if(id==ASYNC_ONE) {

            if (respuestaLogueo.getMensaje().startsWith("Clave de distribuidor")) {
                mClaveDistribuidorView.setError(getString(R.string.error_distribuidor_incorrecto));
                mClaveDistribuidorView.requestFocus();
            } else if (respuestaLogueo.getMensaje().startsWith("Clave de vendedor")) {
                mClaveVendedorView.setError(getString(R.string.error_vendedor_incorrecto));
                mClaveVendedorView.requestFocus();
                mostrarAlerta("Clave de Vendedor Error","ERROR");
            } else {
                mostrarAlerta(respuestaLogueo.getMensaje(),"ERROR");
                //  mostrarAlerta(ctx, ctx.getString(R.string.error_ws_nodisponible),
                //        respuestaLogueo.getMensaje(), false);
            }

        }
    }

    public void checaActualizaciones(){

        try {
            PackageInfo pckginfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);

            Log.e("RVOSPR","version"+pckginfo.versionCode);
            Log.e("RVOSPR","versionNAme"+pckginfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

//        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        //Esto sirve si la actualizacion no se realiza al principio. No es este caso.
       // loadingPanel.setVisibility(View.GONE);
        //Creamos el Autoupdater.
        updater = new Autoupdater(this);
        //Ponemos a correr el ProgressBar.
        //loadingPanel.setVisibility(View.VISIBLE);
        //Ejecutamos el primer metodo del Autoupdater.
        updater.DownloadData(finishBackgroundDownload);

    }


    /**
     * Codigo que se va a ejecutar una vez terminado de bajar los datos.
     */
    private Runnable finishBackgroundDownload = new Runnable() {
        @Override
        public void run() {
            //Volvemos el ProgressBar a invisible.
          //  loadingPanel.setVisibility(View.GONE);
            //Comprueba que halla nueva versión.
            if(updater.isNewVersionAvailable()){
                //Crea mensaje con datos de versión.
                String msj = "Nueva Version Disponible: " + updater.isNewVersionAvailable();
                msj += "\nVersion Actual: " + updater.getCurrentVersionName() + "(" + updater.getCurrentVersionCode() + ")";
                msj += "\nVersion Reciente: " + updater.getLatestVersionName() + "(" + updater.getLatestVersionCode() +")";
                msj += "\n¿Desea Actualizar?";
                //Crea ventana de alerta.
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
                dialog1.setMessage(msj);
                dialog1.setNegativeButton("CANCELAR", null);
                //Establece el boton de Aceptar y que hacer si se selecciona.
                dialog1.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Vuelve a poner el ProgressBar mientras se baja e instala.
            //            loadingPanel.setVisibility(View.VISIBLE);
                        //Se ejecuta el Autoupdater con la orden de instalar. Se puede poner un listener o no
                        updater.InstallNewVersion(null);
                    }
                });

                //Muestra la ventana esperando respuesta.
                dialog1.show();
            }else{
                //No existen Actualizaciones.
                Log.e("RVISORTR","No Hay actualizaciones");
            }
        }
    };


    private void subscribeToPushService() {
        //  FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("promocionesT");

        //   Log.d("AndroidBash", "Subscribed");
        //    Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();

        //    String token = null;

        //      token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        //
        //   Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
        //   Log.d("AndroidBash", token);
    }
}
