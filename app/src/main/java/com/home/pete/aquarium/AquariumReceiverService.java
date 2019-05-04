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

    private static final String TAG = AquariumReceiverService.class.getSimpleName();

    private MqttClient m_client = null;
    private MqttConnectOptions m_options;
    private String m_server;
    private List<String> m_topics;
    private int[] m_qos;
    private String m_clientId = AquariumReceiverService.class.getSimpleName();
    private MemoryPersistence m_dataStore;

    private Notification m_notification = new Notification();

    public boolean m_initialized = false;

    Thread serviceHeartBeatThread;
    private boolean m_continueHeartBeat = true;

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
        startForeground(5131, m_notification);

        m_topics = new ArrayList<String>();
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
                m_initialized + "dataType + " + dataType);

        if (initializeSettings && !this.m_initialized)
        {
            String initializedDate = new Date().toString();
            this.m_initialized = true;
            Log.i(TAG, "Initializing PushReceiver Service for first time: " + initializedDate);
            m_server = "tcp://mqttserver:1883";
            m_dataStore = new MemoryPersistence();

            serviceHeartBeatThread = new Thread()
            {
                public void run()
                {
                    try
                    {
                        while (m_continueHeartBeat)
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
            serviceHeartBeatThread.setName("AquariumHeartBeat");
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
        else if (initializeSettings && this.m_initialized)
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
        MqttTopic topic = m_client.getTopic(topicName);

        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        // Publish the message
        MqttDeliveryToken token = topic.publish(message);

        // Wait until the message has been delivered to the server
        token.waitForCompletion();
        //Log.i(TAG, "Published presence: " + topicName);

    }

    private synchronized void makeConnection() {

        Log.i(TAG, "Making connection to push notification server: " + m_server);

        try {
            if (m_client == null) {
                // Construct the MqttClient instance
                m_client = new MqttClient(m_server, m_clientId, m_dataStore);

                // Set this wrapper as the callback handler
                m_client.setCallback(AquariumReceiverService.this);

                // Construct the object that contains connection parameters
                // such as cleansession and LWAT
                m_options = new MqttConnectOptions();
                m_options.setCleanSession(false);
                m_options.setConnectionTimeout(CONNECTION_TIMEOUT);
                m_options.setKeepAliveInterval(KEEP_ALIVE_INTERVAL);
            }

                if (!m_client.isConnected()) {
                    // Connect to the server
                    Log.i(TAG, "Connecting to " + m_server);
                    m_client.connect(m_options);

                    // Subscribe to the topic
                    String[] topicsArray = new String[m_topics.size()];
                    topicsArray = m_topics.toArray(topicsArray);

                    m_qos = new int[m_topics.size()];
                    for (int i = 0; i < m_topics.size(); i++)
                        m_qos[i] = QOS;

                    Log.i(TAG, "Push isConnected : " + m_client.isConnected());

                    if (m_client.isConnected()) {
                        m_client.subscribe(topicsArray, m_qos);
                        sendHeartBeat();
                        Log.i(TAG, "Subscribed to topic \"" + m_topics + "\" qos " + Arrays.toString(m_qos));
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
        if (m_client != null && m_client.isConnected()) {
            // Disconnect the client
            try {
                // gracefully disconnect by announcing the presence off
                m_client.disconnect();
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
        return m_client != null && m_client.isConnected();
    }

    private void sendHeartBeat() {
        String payload = "{\"value\":\"beat\"}";
        try {
            if (m_client != null) {
                if (m_client.isConnected()) {
                    publish("aquarium/heartbeat", 0, payload.getBytes());
                }
            }
        } catch (MqttException ex) {
            Log.d(TAG, "Mqtt Exception : " + ex.getMessage());
        }
    }
}