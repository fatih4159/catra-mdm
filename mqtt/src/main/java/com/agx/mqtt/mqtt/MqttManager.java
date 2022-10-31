package com.agx.mqtt.mqtt;


import android.util.Log;

import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;


/**
 *
 */
public class MqttManager {
    private static String TAG= "MqttManager";


    private static MqttManager mInstance = null;
    private MqttCallback mCallback;

    // Private instance variables
    private static MqttClient client;
    private MqttConnectionOptions conOpt;
    private boolean clean = true;

    private MqttManager() {
        mCallback = new MqttCallbackBus();
    }

    public static MqttManager getInstance() {
        if (null == mInstance) {
            mInstance = new MqttManager();
        }
        return mInstance;

    }

    /**
     *
     */
    public static void release() {
        Log.d(TAG, "release: ");
        try {
            if (mInstance != null) {
                mInstance.disconnect();
                mInstance = null;
            }
        } catch (Exception e) {

        }
    }

    /**
     *
     * @param brokerUrl
     * @param userName
     * @param password
     * @param clientId
     * @return
     */
    public boolean createConnect(String brokerUrl, String userName, String password, String clientId) {
        Log.d(TAG, "createConnect: ");
        boolean flag = false;
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            // Construct the connection options object that contains connection parameters
            // such as cleanSession and LWT
            conOpt = new MqttConnectionOptions();
            conOpt.setCleanStart(clean);
            conOpt.setReceiveMaximum(null);
            conOpt.setSessionExpiryInterval(0L);
            conOpt.setMaximumPacketSize(null);
            conOpt.setRequestProblemInfo(true);
            conOpt.setRequestResponseInfo(true);

            if (password != null) {
                conOpt.setPassword(password.getBytes());
            }
            if (userName != null) {
                conOpt.setUserName(userName);
            }



            // Construct an MQTT blocking mode client
            client = new MqttClient(brokerUrl, clientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(mCallback);
            flag = doConnect();
            if (flag = true){
                //readLogs();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return flag;
    }

    /**
     *
     * @return
     */
    public boolean doConnect() {
        Log.d(TAG, "doConnect: ");
        boolean flag = false;
        if (client != null) {
            try {
                client.connect(conOpt);
                Log.d("_doConnect","Connected to " + client.getServerURI() + " with client ID " + client.getClientId());
                flag = true;
            } catch (Throwable throwable) {
                Log.e(TAG, "doConnect: "+throwable.getMessage().toString());;
                flag = false;

            }
        }
        return flag;
    }

    /**
     * Publish / send a message to an MQTT server
     *
     * @param topicName the name of the topic to publish to
     * @param qos       the quality of service to delivery the message at (0,1,2)
     * @param payload   the set of bytes to send to the MQTT server
     * @return boolean
     */
    public boolean publish(String topicName, int qos, byte[] payload) {
        Log.d(TAG, "publish: ");

        boolean flag = false;

        if (client != null && client.isConnected()) {

            Log.d("_publish","Publishing to topic " + topicName + "\" qos " + qos);

            // Create and configure a message
            MqttMessage message = new MqttMessage(payload);
            message.setQos(qos);

            // Send the message to the server, control is not returned until
            // it has been delivered to the server meeting the specified
            // quality of service.
            try {
                client.publish(topicName, message);
                flag = true;
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }

        return flag;
    }

    /**
     * Publish / send a message to an MQTT5 server
     *
     * @param topicName the name of the topic to publish to
     * @param qos       the quality of service to delivery the message at (0,1,2)
     * @param payload   the set of bytes to send to the MQTT server
     * @return boolean
     */
    public boolean publish5(String topicName, int qos, byte[] payload) {
        boolean flag = false;
        if (client != null && client.isConnected()) {

            //Log.d("_publish","5Publishing to topic \"" + topicName + "\" qos " + qos);

            // Create and configure a message
            MqttMessage message = new MqttMessage(payload);
            message.setQos(qos);

            // Send the message to the server, control is not returned until
            // it has been delivered to the server meeting the specified
            // quality of service.
            try {
                client.publish(topicName, message);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return flag;
    }

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     *
     * @param topicName to subscribe to (can be wild carded)
     * @param qos       the maximum quality of service to receive messages at for this subscription
     * @return boolean
     */
    public boolean subscribe(String topicName, int qos) {
        Log.d(TAG, "subscribe: ");

        boolean flag = false;


        if (client != null && client.isConnected()) {
            // Subscribe to the requested topic
            // The QoS specified is the maximum level that messages will be sent to the client at.
            // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
            // be downgraded to 1 when delivering to the client but messages published at 1 and 0
            // will be received at the same level they were published at.
            Log.d(TAG, "subscribe: "+"Subscribing to topic \"" + topicName + "\" qos " + qos);
            try {
                client.subscribe(topicName, qos);
                flag = true;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        return flag;

    }

    /**
     *
     * @throws MqttException
     */
    public void disconnect() throws MqttException {
        Log.d(TAG, "disconnect: ");
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

    public static MqttClient getClient() {
        return client;
    }

    public static boolean getConnectionState() {
        if(client != null){
            return client.isConnected();
        }
        return false;
    }
}
