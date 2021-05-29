package org.udg.pds.todoandroid.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.api.LogDescriptor;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.ChooseRegisterLogin;
import org.udg.pds.todoandroid.activity.NavigationActivity;
import org.udg.pds.todoandroid.activity.SplashScreen;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "";

    TodoApi todoApi;

    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        todoApi = ((TodoApp) this.getApplication()).getAPI();

        broadcaster = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent("Notification Data");
        intent.putExtra("title", remoteMessage.getNotification().getTitle());
        intent.putExtra("body", remoteMessage.getNotification().getBody());

        broadcaster.sendBroadcast(intent);

        //sendNotification(remoteMessage);
    }
    /*
    private void sendNotification(RemoteMessage remoteMessage) {
        //Intent intent = new Intent(this, MainActivity.class);
        //PendingIntent pendingIntent =PendingIntent.getActivity(this, m,intent,PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Follow Notifications";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder =
            new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logoavarst)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        //.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }
    */

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        //Fem la crida per actualitzar el token d'aquest usuari en el backend
        Call<String> call = todoApi.updateToken(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                } else {
                    Log.i(TAG, "onResponse: Hi ha hagut un error a l'hora de fer la crida per actulaitzar el token");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG, "onResponse: Hi ha hagut un error a l'hora de fer la crida per actulaitzar el token");
            }
        });
    }

}
