package com.telcel.ractivador.views;

/**
 * Created by PIN7025 on 23/05/2017.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.telcel.ractivador.R;

import java.util.List;


public abstract class BaseAppActivity extends AppCompatActivity implements Validator.ValidationListener, View.OnClickListener {

        protected Validator validator;
        protected boolean validated;
        private ProgressDialog mProgressDialog;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            validator = new Validator(this);
            validator.setValidationListener(this);
        }

        protected boolean validate() {
            if (validator != null)
                validator.validate();
            return validated;           // would be set in one of the callbacks below
        }

        @Override
        public void onValidationSucceeded() {
            validated = true;
        }

        @Override
        public void onValidationFailed(List<ValidationError> errors) {
            validated = false;

            for (ValidationError error : errors) {
                View view = error.getView();
                String message = error.getCollatedErrorMessage(this);


                // Display error messages
                if (view instanceof Spinner) {
                    Spinner sp = (Spinner) view;
                    view = ((LinearLayout) sp.getSelectedView()).getChildAt(0);        // we are actually interested in the text view spinner has
                }

                if (view instanceof TextView) {
                    TextView et = (TextView) view;
                    et.setError(message);
                    et.requestFocus();
                }
            }
        }

        @Override
        public void onClick(View v) {
            validator.validate();
        }


    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void mostrarAlerta2(Context context, String title, String message) {
        final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(context,R.style.myDialog);
        alert.setTitle(title);
        alert.setMessage(message+ "\n"
        );
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //finish();
                //ctx.startActivity(MainActivity);

                //ctx.startActivity(new Intent(ctx, MainActivity.class));
            }
        });
        android.support.v7.app.AlertDialog dialog = alert.create();
        dialog.show();
    }

    protected void mostrarAlerta(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }


    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        // Get the SearchView and set the searchable configuration
    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
        }*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();

        } else if (itemId == R.id.menu_settings) {
            startActivity(new Intent(this, ConsultaActivity.class));

        } else if (itemId == R.id.menu_search) {
            onSearchRequested();

        }

        return super.onOptionsItemSelected(item);
    }


    }

