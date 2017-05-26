package com.telcel.ractivador.net;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import com.telcel.ractivador.MainActivity;
import com.telcel.ractivador.R;


/**
 * Created by PIN7025 on 19/01/2017.
 */
public class Mensaje {

    private Context context;
    private NotificationManager notifyMgr;

    public Mensaje(Context context){
        this.context=context;
    }


    public void getNotificationExito(int id, int iconId, String titulo, String contenido, String telefono, String monto) {
        notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        // Estructura  la notificación
/*
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(iconId).setLargeIcon(
                        BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.ic_telefono

                        ))

                        .setContentTitle(telefono)
                        .setContentText(monto)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        ;
*/

        Notification.Builder builder = new Notification.Builder(context);
      //  Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_bien);

        builder
                .setContentTitle(titulo)
                .setContentText(contenido +" Telefono: "+telefono+" monto:"+monto)
                .setContentInfo("mas informacion de la activacion")
        //        .setSmallIcon(R.mipmap.ic_telefononube)
                .setWhen(System.currentTimeMillis())
          //      .setLargeIcon(bm)
                .setTicker("Activacion exitosa");

        ;
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        //.setLargeIcon(bitmapIcon);

        new Notification.BigTextStyle(builder)
                .bigText(contenido +" Telefono: "+telefono+" monto:"+monto)
                .setBigContentTitle("Mensaje de Activacion")
                .setSummaryText("Resultado de Activacion")
                .build();


        // Crear intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


// API 11 o mayor
        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS);
        builder.setLights(Color.YELLOW, 300, 100);
        //  builder.setVibrate(new long[] {0,100,200,300});

        // Construir la notificación y emitirla
        notifyMgr.notify(id, builder.build());
    }


    public void getMostrarAlerta(Context context, String title, String message, String titlePositiveButton) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.myDialog);
        alert.setTitle(title);
        alert.setMessage(message+ "\n"
        );
        alert.setPositiveButton(titlePositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                // finish();
                // startActivity(getIntent());
                dialog.dismiss();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void getNotificationError(int id, int iconId, String titulo, String contenido) {
        notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
      //  Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_mal);

        builder
                .setContentTitle(titulo)
                .setContentText(contenido)
                .setContentInfo("mas informacion de la activacion")
          //      .setSmallIcon(R.mipmap.ic_telefononube)
                .setWhen(System.currentTimeMillis())
            //    .setLargeIcon(bm)
                .setTicker("Error en la Activacion");

        ;

        //.setLargeIcon(bitmapIcon);

        new Notification.BigTextStyle(builder)
                .bigText(contenido)
                .setBigContentTitle("Mensaje de Activacion")
                .setSummaryText("Resultado de Activacion")
                .build();


        // Crear intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


// API 11 o mayor
        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS);
        builder.setLights(Color.RED, 300, 100);
        //  builder.setVibrate(new long[] {0,100,200,300});
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        // Construir la notificación y emitirla
        notifyMgr.notify(id, builder.build());
    }


}
