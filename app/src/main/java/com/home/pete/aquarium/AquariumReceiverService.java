package com.home.pete.aquarium;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AquariumReceiverService extends Service implements MqttCallback {
    private static final int KEEP_ALIVE_INTERVAL = 15;
    private static final int CONNECTION_TIMEOUT = 15;
    private static final int QOS = 2;

    private static final String TAG = "PushService";

    //Timers
    private static java.util.Timer statusTimer;
    private static java.util.Timer heartbeatTimer;


    private MqttClient client = null;
    private MqttConnectOptions conOpt;
    private String brokerUrl;
    private List<String> topics;
    private static String clientId = "LF";
    private static String facilityId;
    private String equipmentId;
    private int[] qos;
    private MemoryPersistence dataStore;
    private String userAgent = "";
    private boolean isConnectedToLive = true;
    private static String deviceType = "";
    private String status = "Idle";
    private String userId = "Anonymous";

    private Notification notification = new Notification();

    /**
     * True if service has been initialized once.
     * False otherwise.
     */
    public boolean initialized = false;

    Thread serviceHeartBeatThread;
    private String initializedDate;
    private boolean contServerHB = true;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        // always foreground
        startForeground(5131, notification);

        topics = new ArrayList<String>();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean initializeSettings = intent.getBooleanExtra("initializing", false);
        String dataType = intent.getStringExtra("dataType");

        //Fill this in if null.  We use it for debugging below
        //and it can be null in certain cases
        if (dataType == null)
        {
            dataType = "invalidDataType";
        }

        Log.i(TAG, "onStartCommand: initializeSettings: " + initializeSettings + " initialized: " +
                initialized + "dataType + " + dataType);

        if (initializeSettings && !this.initialized)
        {
            this.initializedDate = new Date().toString();
            this.initialized = true;
            Log.i(TAG, "Initializing PushReceiver Service for first time: " + initializedDate);
            //This version uses SSL.
            brokerUrl = "ssl://" + intent.getStringExtra("server") + ":" + intent.getStringExtra("port");
            dataStore = new MemoryPersistence();

            serviceHeartBeatThread = new Thread()
            {
                public void run()
                {
                    try
                    {
                        while (contServerHB)
                        {
                            Thread.sleep(10000);
                            sendHeartBeat();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
            };
            serviceHeartBeatThread.setName("PushServiceHeartBeat");
            serviceHeartBeatThread.start();

            Thread t2 = new Thread()
            {
                public void run()
                {
                    makeConnection();
                    if (isConnected())
                    {
                        sendHeartBeat();
                    }

                }
            };
            t2.start();
        }
        else if (initializeSettings && this.initialized)
        {
            Log.d(TAG,"Push service started while already initialized, ignoring");
        }
        else
        {
            Log.e(TAG,"Invalid push onStart intent");
        }
        return START_NOT_STICKY;
    }


    /**
     * Performs a single publish
     *
     * @param topicName the topic to publish to
     * @param qos       the qos to publish at
     * @param payload   the payload of the message to publish
     * @throws MqttException What happens when we fail
     */
    private void publish(String topicName, int qos, byte[] payload) throws MqttException {
        // Get an instance of the topic
        MqttTopic topic = client.getTopic(topicName);

        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        // Publish the message
        MqttDeliveryToken token = topic.publish(message);

        // Wait until the message has been delivered to the server
        token.waitForCompletion();
        //Log.i(TAG, "Published presence: " + topicName);

    }

    private synchronized void makeConnection() {

        Log.i(TAG, "Making connection to push notification server: " + brokerUrl);

        try {
            if (client == null) {
                // Construct the MqttClient instance
                client = new MqttClient(brokerUrl, clientId, dataStore);

                // Set this wrapper as the callback handler
                client.setCallback(AquariumReceiverService.this);

                // Construct the object that contains connection parameters
                // such as cleansession and LWAT
                conOpt = new MqttConnectOptions();
                conOpt.setCleanSession(false);
                conOpt.setConnectionTimeout(CONNECTION_TIMEOUT);
                conOpt.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);
            }

                if (!client.isConnected()) {
                    // Connect to the server
                    Log.i(TAG, "Connecting to " + brokerUrl);
                    client.connect(conOpt);

                    // Subscribe to the topic
                    String[] topicsArray = new String[topics.size()];
                    topicsArray = topics.toArray(topicsArray);

                    qos = new int[topics.size()];
                    for (int i = 0; i < topics.size(); i++)
                        qos[i] = QOS;

                    Log.i(TAG, "Push isConnected : " + client.isConnected());

                    if (client.isConnected()) {
                        client.subscribe(topicsArray, qos);
                        sendHeartBeat();
                        Log.i(TAG, "Subscribed to topic \"" + topics + "\" qos " + Arrays.toString(qos));
                    }
            } else {
                Log.i(TAG, "No internet connection detected, waiting on connecting to push server until connection detected");
            }

        } catch (MqttException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to set up client: " + e.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error. Unable to set up client: " + e.toString());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        if (client != null && client.isConnected()) {
            // Disconnect the client
            try {
                // gracefully disconnect by announcing the presence off
                client.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error in disconnect client: " + e.toString());

            }
            Log.i(TAG, "Disconnected");
        }

        super.onDestroy();

        System.exit(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.
     * Throwable)
     */
    @Override
    public void connectionLost(Throwable cause) {
        Log.i(TAG, "connectionLost : " + cause);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(org.eclipse
     * .paho.client.mqttv3.MqttTopic,
     * org.eclipse.paho.client.mqttv3.MqttMessage)
     */
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        if (s.equals("aquarium/temperature")) {
            Double value = Double.valueOf(new String(mqttMessage.getPayload()));
            String result = value.toString();
            Log.d(TAG, result);
        }
        if (s.equals("aquarium/waterlevel")) {
            Double value = Double.valueOf(new String(mqttMessage.getPayload()));
            String result = value.toString();
            Log.d(TAG, result);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse
     * .paho.client.mqttv3.MqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
    {
        Log.d(TAG, "delivery complete");
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    private void sendHeartBeat() {
        String payload = "{\"value\":\"beat\"}";
        try {
            if (client != null) {
                if (client.isConnected()) {
                    publish("aquarium/heartbeat", 0, payload.getBytes());
                }
            }
        } catch (MqttException ex) {
            Log.d(TAG, "Mqtt Exception : " + ex.getMessage());
        }
    }
}