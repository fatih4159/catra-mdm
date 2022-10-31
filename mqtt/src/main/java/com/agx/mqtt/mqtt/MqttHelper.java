package com.agx.mqtt.mqtt;

import static com.agx.mqtt.helper.Utils.getUniqueDeviceID;
import static com.agx.mqtt.mqtt.MqttManager.getConnectionState;
import static com.agx.mqtt.ui.MainService.getActivity;
import static com.agx.mqtt.ui.MainService.getStarter;


import android.util.Log;

import com.agx.mqtt.helper.Convert;
import com.agx.mqtt.helper.Utils;

import java.util.HashMap;

public class MqttHelper {
    public final static String TAG ="MqttHelper";

    public final static String MQTT_HOST = "192.168.1.53";
    public final static String MQTT_LOGIN = "device";
    public final static String MQTT_PASS = "";
    public final static String MQTT_PORT = "1883";
    //public final static String MQTT_URL ="ssl://"+MQTT_HOST+":"+MQTT_PORT;
    public final static String MQTT_URL ="tcp://"+MQTT_HOST+":"+MQTT_PORT;

    public final static String[] MQTT_TOPICS = {
            "/",
            getUniqueDeviceID()
    };
    public static String SERIAL = getUniqueDeviceID();

    public static boolean start_connect() {

        try {
            SERIAL = getUniqueDeviceID();

            boolean isConnectionCreated= MqttManager.getInstance().createConnect(MQTT_URL, MQTT_LOGIN, MQTT_PASS, SERIAL);
            if(isConnectionCreated) {


                Log.d("_Status", "isConnected: " + getConnectionState());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getConnectionState()) {
                            Log.d(TAG, "start_connect: Connected");
//                            getBinding().ivMqttIndicator.setBackgroundColor(Color.GREEN);
                        } else {
                            Log.d(TAG, "start_connect:  NOT Connected");
//                            getBinding().ivMqttIndicator.setBackgroundColor(Color.RED);
                        }
                    }
                });
                if (!getConnectionState()){
                    // if its still not connected
                    return false;
                }
                MqttManager.getInstance().subscribe("/", 2);
                MqttManager.getInstance().subscribe(getUniqueDeviceID()+"/#", 2);
//                MqttManager.getInstance().subscribe("/"+getUniqueDeviceID()+"/log", 2);
//                MqttManager.getInstance().subscribe("/"+getUniqueDeviceID()+"/location", 2);

//                publish("","Connected");
                String ip = Utils.getIPAddress(true);
                getStarter().publish(getUniqueDeviceID(),"Connected@"+ip,null);

                return true;
            }else {
                Log.d(TAG, "start_connect: connection not created");
                return false;
            }
        }catch (Throwable throwable){
         return false;
        }
    }

    public static void disconnect(){
        MqttManager.release();
    }

    public static void publish(String msg,String tpc, HashMap<String,String> xtr) {
                SERIAL = getUniqueDeviceID();

                HashMap<String,String> payloadMap = new HashMap<>();
                payloadMap.put("msg",msg);

                if(xtr != null){
                    Log.d(TAG, "publish: extras found");
                    payloadMap.putAll(xtr);
                }

                String json_payload = Convert.HashMapToJson(payloadMap);



                byte[] payload_byte= msg.getBytes();

                if(tpc.isEmpty()) {
                    MqttManager.getInstance().publish(MQTT_TOPICS[0], 2, json_payload.getBytes());
                }else{
                    MqttManager.getInstance().publish(tpc, 2, json_payload.getBytes());

                }


//                getBinding().svLog.fullScroll(View.FOCUS_DOWN);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        getBinding().etvMessage.setText("");

                    }
                });

//        getBinding().svLog.fullScroll(View.FOCUS_DOWN);
    }



}
