package com.telcel.ractivador;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.telcel.ractivador.net.Conexion;
import com.telcel.ractivador.pojo.RespuestaLogueo;
import com.telcel.ractivador.session.SessionManager;
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
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
}
