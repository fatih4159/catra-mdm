package com.agx.mqtt.helper;

import static com.agx.mqtt.helper.Constants.CHANNEL_ID;
import static com.agx.mqtt.ui.MainService.getActivity;
import static com.agx.mqtt.ui.MainService.getContext;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.agx.mqtt.R;

import java.util.Random;


public class Notification {


    public static String CHANNEL_COMMON = "NOTIFICATION_CHANNEL_CATRA";

    private static NotificationManagerCompat notificationManagerCompat;
    private static NotificationManager notificationManager;


    public static void notificate(String notificationTitle, String notificationContent, int notificationCompatPriority, Integer icon) {
        int m = CHANNEL_COMMON.length();

        if (notificationManagerCompat == null){
            notificationManagerCompat = NotificationManagerCompat.from(getActivity().getApplicationContext());
        }
        if(notificationManager.getActiveNotifications().length>1) {
            Log.d("Notifications", "ActiveNotifications:" + notificationManager.getActiveNotifications().length);
            notificationManager.cancel(m);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),CHANNEL_COMMON)
                .setAllowSystemGeneratedContextualActions(true)
                .setTicker(CHANNEL_COMMON)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setPriority(notificationCompatPriority)
                .setSubText(CHANNEL_COMMON)
                .setSmallIcon(icon)
                .setPriority(notificationCompatPriority)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);



        Random random = new Random();
        //int m = random.nextInt(9999 - 1000) + 1000;
        Log.d("NotificationID_"+CHANNEL_COMMON,"ID:"+m);
        // notificationId is a unique int for each notification that you must define
        notificationManagerCompat.notify(m, builder.build());
    }


    /**Creating a Notification Channel for the Notification Manager
     *
     */
    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_MAX;
            @SuppressLint("WrongConstant") NotificationChannel channel1 = new NotificationChannel(CHANNEL_COMMON, CHANNEL_COMMON, NotificationManager.IMPORTANCE_MAX);

            channel1.setDescription(description);


            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);

        }
    }

}
