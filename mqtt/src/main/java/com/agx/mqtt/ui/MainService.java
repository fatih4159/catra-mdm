package com.agx.mqtt.ui;



import static com.agx.mqtt.helper.Notification.createNotificationChannel;
import static com.agx.mqtt.helper.Notification.notificate;
import static com.agx.mqtt.helper.Utils.getUniqueDeviceID;
import static com.agx.mqtt.mqtt.MqttManager.getConnectionState;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;


import com.agx.mqtt.R;
import com.agx.mqtt.helper.GPSLocator;
import com.agx.mqtt.worker.WorkStarter;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainService extends Service {
    private static final String TAG ="MainService";

    private static Service mService;
    public static Service getService(){
        return mService;
    };

    private static WorkStarter mStarter;

    private final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private AlertDialog enableNotificationListenerAlertDialog;

    private static AppCompatActivity remoteActivity;



    private static  Service context;
    private static View view;
    List<String> askPerms = new ArrayList<>(Arrays.asList(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PRECISE_PHONE_STATE
    ));
    List<String> missingPerms = new ArrayList<>();




    @Override
    public void onCreate() {
        super.onCreate();
        mStarter = new WorkStarter(getActivity());
        mService = this;


        try{
            new GPSLocator();

        }catch (Exception e){}



        if(!getConnectionState()){
            mStarter.connect();
        }

        createNotificationChannel(getContext());


//        binding.etvMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                                             @Override
//                                             public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                                                 switch (i){
//                                                     case EditorInfo.IME_ACTION_SEND:
//                                                         mStarter.publish("",binding.etvMessage.getText().toString(),null);
//                                                         //publish(binding.etvMessage.getText().toString());
//                                                         break;
//                                                 }
//                                                 return false;
//                                             }
//                                         });
//
//                //view = findViewById(R.layout.activity_main);
//
//                //startLockTask();
//
//                context = this;
//        //tv_log= (TextView) findViewById(R.id.tv_log);
//
//        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //publish(binding.etvMessage.getText().toString());
//                mStarter.publish("",binding.etvMessage.getText().toString(),null);
//
//            }
//        });



    }






    @Subscribe
    public void onEvent(MqttMessage message) {
        Log.d("_INCOMING",message.toString());

    }

//    @Override
//    public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
//        super.onTopResumedActivityChanged(isTopResumedActivity);
//        Log.d("_Lifecycle","onTopResumedActivityChanged");
//    }






    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy: ");    
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.d(TAG, "onKeyDown: ");
//        Log.i("KEYPRESS","Keycode: "+keyCode +"|"+ "KeyEvent: "+event );
//
//        switch (keyCode){
//            case KeyEvent.KEYCODE_HOME:
//                //@SuppressLint("ResourceType") BaseInputConnection  mInputConnection = new BaseInputConnection( findViewById(R.layout.activity_main), true);
//                //KeyEvent kd = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
//                //mInputConnection.sendKeyEvent(kd);
//
//                Intent switchActivityIntent = new Intent(this, MainService.class);
//                startActivity(switchActivityIntent);
//                break;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private boolean checkPermissionsGranted(List<String> checkList, List<String> notGrantedList){
        Log.d(TAG, "checkPermissionsGranted: ");
        Log.d("checkPermissionsGranted","Started");
        boolean resi = true;
        notGrantedList.clear();
        for (int i=0; i<checkList.size(); i++){
            String perm = checkList.get(i);
            if (ContextCompat.checkSelfPermission( getContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                notGrantedList.add(perm);
                resi = false;
            }
        }

        Log.d("checkPermissionsGranted","Finished");
        return resi;
    }

    public static WorkStarter getStarter() {
        return mStarter;
    }



    public static void setRemoteActivity(AppCompatActivity remoteActivity) {
        MainService.remoteActivity = remoteActivity;
    }

    public static AppCompatActivity getContext(){
        return remoteActivity;
    }
    public static AppCompatActivity getActivity(){
        return remoteActivity;
    }
    public class MyBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
        Service m_service;


        private ServiceConnection m_serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                m_service = ((MainService.MyBinder)service).getService();
            }

            public void onServiceDisconnected(ComponentName className) {
                m_service = null;
            }
        };


    }

}
