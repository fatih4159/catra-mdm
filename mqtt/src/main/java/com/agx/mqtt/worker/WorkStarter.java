package com.agx.mqtt.worker;

import static com.agx.mqtt.helper.Utils.getUniqueDeviceID;
import static com.agx.mqtt.ui.MainService.getStarter;
import static com.agx.mqtt.worker.Works.DoInstall.WTAG_INSTALL_LINK;
import static com.agx.mqtt.worker.Works.ShowNotification.WTAG_NOTIF_MSG;
import static com.agx.mqtt.worker.Works.StartDownload.WTAG_DOWNLOADLINK;
import static com.agx.mqtt.worker.Works.PublishMessage.WTAG_PUBLISH_EXTRAS;
import static com.agx.mqtt.worker.Works.PublishMessage.WTAG_PUBLISH_MESSAGE;
import static com.agx.mqtt.worker.Works.PublishMessage.WTAG_PUBLISH_TOPIC;
import static com.agx.mqtt.worker.Works.RunAdbCommand.WTAG_ADBCOMMAND;
import static com.agx.mqtt.worker.Works.ShowNotification.WTAG_NOTIF_TITLE;

import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.agx.mqtt.helper.Convert;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WorkStarter {
    private static final String TAG = "WorkStarter";
    public static final String WTAG_CONNECT = "CONNECT";
    public static final String WTAG_DISCONNECT = "DISCONNECT";


    private final Context mContext;
    private static WorkManager mWorkManager;



    public static WorkManager getWorkManager() {
        return mWorkManager;
    }

    public WorkStarter(Context context) {
        mContext = context;
        mWorkManager = WorkManager.getInstance(context);
    }

    public WorkStarter showNotification(String title,String msg){
        Log.d(TAG, "showToast: enqueueing");

        Data.Builder data = new Data.Builder();
        data.putString(WTAG_NOTIF_TITLE, msg);
        data.putString(WTAG_NOTIF_MSG, msg);
        getStarter().publish(getUniqueDeviceID()+"/log", "Notification request received",null);
        mWorkManager.enqueue(new OneTimeWorkRequest.Builder(Works.ShowNotification.class)
                .addTag(WTAG_NOTIF_TITLE)
                .setInputData(data.build())
                .build());
        Log.d(TAG, "showToast: enqueued");


        return this;
    }
    public WorkStarter startDownload(String link){
        Log.d(TAG, "showToast: enqueueing");

        Data.Builder data = new Data.Builder();
        data.putString(WTAG_DOWNLOADLINK, link);

        mWorkManager.enqueue(new OneTimeWorkRequest.Builder(Works.StartDownload.class)
                .addTag(WTAG_DOWNLOADLINK)
                .setInputData(data.build())
                .build());
        Log.d(TAG, "startDownload: enqueued");


        return this;
    }
    public WorkStarter vibrate(){
        Log.d(TAG, "vibrate: enqueueing");
        mWorkManager.enqueue(new OneTimeWorkRequest.Builder(Works.Vibrate.class).build());
        Log.d(TAG, "vibrate: enqueued");


        return this;
    }
    public WorkStarter reboot(){
        Log.d(TAG, "reboot: enqueueing");
        mWorkManager.enqueue(new OneTimeWorkRequest.Builder(Works.Reboot.class).build());
        Log.d(TAG, "reboot: enqueued");


        return this;
    }

    public WorkStarter wake(){
        Log.d(TAG, "wake: enqueueing");
        mWorkManager.enqueue(new OneTimeWorkRequest.Builder(Works.WakeScreen.class).build());
        Log.d(TAG, "wake: enqueued");


        return this;
    }
    public WorkStarter runADB(String adbCommand){
        Log.d(TAG, "runADB: enqueueing");

        Data.Builder data = new Data.Builder();
        data.putString(WTAG_ADBCOMMAND, adbCommand);

        mWorkManager.enqueueUniqueWork(
                "runADB",
                ExistingWorkPolicy.REPLACE,
                new OneTimeWorkRequest.Builder(Works.RunAdbCommand.class)
                        .addTag(WTAG_ADBCOMMAND)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS
                        )
                        .setInputData(data.build())
                        .build());
        Log.d(TAG, "runADB: enqueued");


        return this;
    }
    public WorkStarter install(String apkLink){
        Log.d(TAG, "showToast: enqueueing");

        Data.Builder data = new Data.Builder();
        data.putString(WTAG_INSTALL_LINK, apkLink);

        mWorkManager.enqueue(new OneTimeWorkRequest.Builder(Works.DoInstall.class)
                .addTag(WTAG_INSTALL_LINK)
                .setInputData(data.build())
                .build());
        Log.d(TAG, "showToast: enqueued");


        return this;
    }



    //MQTT Stuff
    public WorkStarter connect(){
        Log.d(TAG, "connect: enqueueing");

        mWorkManager.enqueueUniqueWork(
                "connect",
                ExistingWorkPolicy.KEEP,
                new OneTimeWorkRequest.Builder(Works.ConnectMQTT.class)
                        .addTag(WTAG_CONNECT)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS
                        )
                        .build());
        Log.d(TAG, "connect: enqueued");
        return this;
    }
    public WorkStarter disconnect(){
        Log.d(TAG, "disconnect: enqueueing");
        mWorkManager.enqueue(new OneTimeWorkRequest.Builder(Works.DisconnectMQTT.class)
                        .addTag(WTAG_DISCONNECT)
                .build());
        Log.d(TAG, "disconnect: enqueued");


        return this;
    }

    public WorkStarter publish(String topic, String message, HashMap<String, String> extras){
        Log.d(TAG, "publish: enqueueing");

        String jsonExtras = Convert.HashMapToJson(extras);

        Data.Builder data = new Data.Builder();
        data.putString(WTAG_PUBLISH_MESSAGE, message);
        data.putString(WTAG_PUBLISH_TOPIC,"/"+topic);
        data.putString(WTAG_PUBLISH_EXTRAS,jsonExtras);


        mWorkManager.enqueueUniqueWork(
                "publishMessage",
                ExistingWorkPolicy.REPLACE,
                new OneTimeWorkRequest.Builder(Works.PublishMessage.class)
                        .addTag(WTAG_PUBLISH_MESSAGE)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS
                        )
                        .setInputData(data.build())
                        .build());
        Log.d(TAG, "publish: enqueued");


        return this;
    }



}
