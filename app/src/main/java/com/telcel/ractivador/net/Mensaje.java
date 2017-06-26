package com.telcel.ractivador.net;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

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


    private void sendNotification(Context ctx,String messageBody, Bitmap image, String TrueOrFalse, String paginaParaDireccionar, Bitmap imageIcon, String ticker, String tituloNotificacion, String sumario, String tituloInterior) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx)
                .setLargeIcon(image)/*Notification icon image*/
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(tituloNotificacion)
                .setContentText(messageBody)
                .setLargeIcon(imageIcon)
                .setTicker(ticker)
                .setStyle(new NotificationCompat.BigPictureStyle().setBigContentTitle(tituloInterior)
                        .bigPicture(image).setSummaryText(sumario))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                ;
        notificationBuilder.setVibrate(new long[] { 100, 200, 100, 500 });
        // API 11 o mayor
        //    notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS);
        notificationBuilder.setLights(Color.YELLOW, 300, 100);


        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
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
