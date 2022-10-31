package com.agx.mqtt.worker;

import static android.content.Context.POWER_SERVICE;


import static com.agx.mqtt.helper.Downloader.downloadFile;
import static com.agx.mqtt.helper.Notification.notificate;
import static com.agx.mqtt.helper.Utils.getUniqueDeviceID;
import static com.agx.mqtt.ui.MainService.getActivity;
import static com.agx.mqtt.ui.MainService.getStarter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.agx.mqtt.R;
import com.agx.mqtt.helper.Convert;
import com.agx.mqtt.helper.Installer;
import com.agx.mqtt.helper.Utils;
import com.agx.mqtt.mqtt.MqttHelper;
import com.agx.mqtt.mqtt.MqttManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Works {
    public final static String MQTT_HOST = "192.168.1.53";
    public final static String MQTT_LOGIN = null;
    public final static String MQTT_PASS = null;
    public final static String MQTT_PORT = "9001";

    private static PowerManager mPowerManager;
    private static PowerManager.WakeLock mWakeLock;

    public static class ExampleWorker extends Worker {
        public ExampleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            mContext = context;
            mParams = workerParams;
        }
        Context mContext;
        WorkerParameters mParams;

        @NonNull
        @Override
        public Result doWork() {
            try{
                return Result.success();
            }catch(Throwable throwable){

                return Result.failure();
            }
        }
    }
    public static class RunAdbCommand extends Worker {
        public static final String WTAG_ADBCOMMAND = "ADBCOMMAND";
        private static final String TAG = "RunAdbCommand";
        Context mContext;
        WorkerParameters mParams;
        public AtomicBoolean run = new AtomicBoolean();
        public static String log  = "";


        public RunAdbCommand(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            mContext = context;
            mParams = workerParams;
        }

        @NonNull
        @Override
        public Result doWork() {
            try{
                try {
                    getStarter().publish(getUniqueDeviceID()+"/log", "run ADB-Command request received",null);

                    String commandToRun = mParams.getInputData().getString(WTAG_ADBCOMMAND);
                    Process process;
                    BufferedReader bufferedReader = null;
                    String logLines = "";

                    try {
                        process = Runtime.getRuntime().exec(commandToRun); // -d stands for close stream after execution NOT DEBUG!!!
                        bufferedReader = new BufferedReader(
                                new InputStreamReader( process.getInputStream() )
                        );
                        logLines = bufferedReader.lines().collect(Collectors.joining("\n"));

                    } catch (Exception e) {/* eat it up*/}

                    List<String> logsList= Arrays.asList(logLines.split("\n"));


                    logsList.forEach(v ->{
                        String topic = getUniqueDeviceID()+"/logcat";
                        String message = v;
                        HashMap<String,String> extras = new HashMap<>();


                        getStarter().publish(topic,message,extras);


                    });

                }catch (Exception e){
                    e.printStackTrace();
                }



                return Result.success();
            }catch(Throwable throwable){

                return Result.failure();
            }
        }
    }
    public static class ShowNotification extends Worker {
        public static final String WTAG_NOTIF_TITLE = "NOTIF_TITLE";
        public static final String WTAG_NOTIF_MSG = "NOTIF_TITLE";


        public ShowNotification(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            mContext = context;
            mParams = workerParams;
        }
        WorkerParameters mParams;
        Context mContext;

        @NonNull
        @Override
        public Result doWork() {
            try {
                String title = mParams.getInputData().getString(WTAG_NOTIF_TITLE);
                String msg = mParams.getInputData().getString(WTAG_NOTIF_MSG);


                notificate(title,msg, NotificationCompat.PRIORITY_HIGH, R.drawable.ic_android_black_24dp);


                return Result.success();
            }catch (Throwable throwble){


                throwble.printStackTrace();
                return Result.failure();
            }
        }
    }
    public static class StartDownload extends Worker {
        public static final String WTAG_DOWNLOADLINK = "DOWNLOADWORKER";

        public StartDownload(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            mContext = context;
            mParams = workerParams;
        }
        WorkerParameters mParams;
        Context mContext;
        String TAG = "DownloadWorker";

        @NonNull
        @Override
        public Result doWork() {
            try {
                String link = mParams.getInputData().getString(WTAG_DOWNLOADLINK);

                String out_prefix = Utils.Link.getPrefix(link);
                String out_suffix = Utils.Link.getSuffix(link);

                File downloadDir = File.createTempFile(out_prefix,out_suffix, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));


                Log.d(TAG, "downloading link:"+link);
                Log.d(TAG, "downloading File:"+out_prefix+"."+out_suffix);


                downloadFile(link,downloadDir);
                Log.d(TAG, "File Downloaded to:"+downloadDir.toString());



                return Result.success();
            }catch (Exception e){


                e.printStackTrace();
                return Result.failure();
            }
        }
    }


    public static class Vibrate extends Worker {
        public Vibrate(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @SuppressLint("NewApi")
        @NonNull
        @Override
        public Result doWork() {
            try{
                HashMap<String,String > extras = new HashMap<>();
                String topic = getUniqueDeviceID()+"/log";
                String message = "vibration response";

                extras.put("type","vibration");

                // Get instance of Vibrator from current Context
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

                // Vibrate for 400 milliseconds
                v.vibrate(VibrationEffect.createOneShot(400,100));
                extras.put("vibrated","true");


                getStarter().publish(topic, message,extras);

                return Result.success();
            }catch(Throwable throwable){

                return Result.failure();
            }

        }
    }
    public static class Reboot extends Worker {
        Context context;
        WorkerParameters workerParameters;
        public Reboot(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            this.context = context;
            this.workerParameters = workerParams;
        }

        @NonNull
        @Override
        public Result doWork() {
            try{
                PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
                pm.reboot(null);


                return Result.success();
            }catch(Exception e){
                e.printStackTrace();
                getStarter().publish(getUniqueDeviceID(),"REBOOT FAILED!",null);

                return Result.failure();
            }

        }
    }


    public static class WakeScreen extends Worker {

        public WakeScreen(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            mContext = context;
        }
        Context mContext;

        @NonNull
        @Override
        public Result doWork() {
            try {
                PowerManager.WakeLock screenLock =    ((PowerManager)getActivity().getSystemService(POWER_SERVICE)).newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "mqtt:wakeup");
                screenLock.acquire(10*60*100L /*10 minutes*/);
                screenLock.release();
                return Result.success();
            }catch (Throwable throwble){


                throwble.printStackTrace();
                return Result.failure();
            }
        }
    }

    public static class ConnectMQTT extends Worker {
        public ConnectMQTT(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        private static final String TAG = "ConnectMQTT";
        private static int attempts =0;
        private static final int max_attempts =99999;
        private static long startTime =0;
        private static float diffrenceINSec = 0;


        @NonNull
        @Override
        public Result doWork() {
            getLastTime();
            Log.d(TAG, "doWork: startTime:"+startTime);



            try{
                Log.d(TAG, "doWork: starting connection");
                boolean connection = MqttHelper.start_connect();
                if(connection){
//                    snackOnTop("Connected");
                    return Result.success();
                }else{
                    if (attempts < max_attempts){
                        String ATEMPTSTRING = "Connection failed, retrying... \n (attempts "+ attempts +" , last "+diffrenceINSec+" seconds ago)";
                        Log.d(TAG, "doWork:"+ATEMPTSTRING);
//                        snackOnTop(ATEMPTSTRING);
                        attempts++;
                        //lastAttempt = LocalDateTime.now();
                        return Result.retry();
                    }else {
                        attempts = 0;
                        Log.d(TAG, "doWork: connection not possible, max attempts (attempt "+ attempts +")");

                        return Result.failure();
                    }
                }

            }catch(Throwable throwable){
                return Result.failure();
            }
        }

        private void getLastTime() {
            if(startTime == 0){
                startTime= System.currentTimeMillis();
                //Log.d(TAG, "doWork: diffrenceINSec:"+"00.0");

            }else{
                long difference = System.currentTimeMillis() - startTime;
                diffrenceINSec = difference /1000;
                //Log.d(TAG, "doWork: diffrenceINSec:"+diffrenceINSec);
                startTime= System.currentTimeMillis();

            }
        }


    }

    public static class DisconnectMQTT extends Worker {
        public DisconnectMQTT(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }


        @NonNull
        @Override
        public Result doWork() {
            try{
                MqttManager.release();
                return Result.success();
            }catch(Throwable throwable){

                return Result.failure();
            }
        }


    }

    public static class PublishMessage extends Worker {
        public PublishMessage(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            mContext = context;
            mParams = workerParams;
        }
        Context mContext;
        WorkerParameters mParams;
        public static final String WTAG_PUBLISH_MESSAGE = "PUBLISH_MESSAGE";
        public static final String WTAG_PUBLISH_TOPIC = "PUBLISH_TOPIC";
        public static final String WTAG_PUBLISH_EXTRAS = "PUBLISH_EXTRAS";


        private static final String TAG = "PublishMessage";


        @NonNull
        @Override
        public Result doWork() {
            try{
                HashMap<String,String> xtr = new HashMap<>();
                String msg = mParams.getInputData().getString(WTAG_PUBLISH_MESSAGE);
                String tpc = mParams.getInputData().getString(WTAG_PUBLISH_TOPIC);
                String ext = mParams.getInputData().getString(WTAG_PUBLISH_EXTRAS);

                if(ext != null){
                    xtr = Convert.HashMapFromJson(ext);
                    Log.d(TAG, "doWork: extras found");
                }

                Log.d(TAG, "doWork: try publishing message"+msg);

                MqttHelper.publish(msg,tpc,xtr);


                return Result.success();
            }catch(Throwable throwable){

                return Result.failure();
            }
        }
    }

    public static class DoInstall extends Worker {
        public DoInstall(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            mContext = context;
            mParams = workerParams;
        }
        Context mContext;
        WorkerParameters mParams;
        public static final String WTAG_INSTALL_LINK = "INSTALL_LINK";



        private static final String TAG = "DoInstall";


        @NonNull
        @Override
        public Result doWork() {
            String link = mParams.getInputData().getString(WTAG_INSTALL_LINK);

            try{
                Log.d(TAG, "doWork: try installing apk");

                Installer.installPackage(mContext,link);
                getStarter().publish(getUniqueDeviceID(),"Installation Succeeded:"+link,null);

                return Result.success();
            }catch(Exception e){
                e.printStackTrace();
                getStarter().publish(getUniqueDeviceID(),"Installation Failed:"+link,null);

                return Result.failure();
            }
        }
    }










}


