package com.home.pete.aquarium;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.home.pete.aquarium.Constants;

public class AquariumReceiverService extends Service implements MqttCallback
{
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

    private final IBinder m_binder = new MyLocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return m_binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        startMyOwnForeground();

        m_topics = new ArrayList<String>();
        m_topics.add(Constants.TEMPERATURE_TOPIC);
        m_topics.add(Constants.WATERLEVEL_TOPIC);
        m_topics.add(Constants.CONTROLS_TOPIC);
        m_topics.add(Constants.DATABASE_TOPIC);
        LocalBroadcastManager.getInstance(this).registerReceiver(databaseRequest, new IntentFilter("temperature"));
    }

    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.home.pete.aquarium";
        String channelName = "Paho MQTT Receiver";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.fish)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        if (!this.m_initialized)
        {
            String initializedDate = new Date().toString();
            this.m_initialized = true;
            Log.i(TAG, "Initializing PushReceiver Service for first time: " + initializedDate);
            m_server = Constants.MQTT_BROKER_URL;
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
        else if (this.m_initialized)
        {
            Log.d(TAG,"Push service started while already initialized, ignoring");
        }
        else
        {
            Log.e(TAG,"Invalid push onStart intent");
        }
        return START_NOT_STICKY;
    }

    public void publishMessage(String topic, int qos, byte[] payload) {
        try {
            publish(topic, qos, payload);
        }
        catch (MqttException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Performs a single publish
     *
     * @param topic the topic to publish to
     * @param qos       the qos to publish at
     * @param payload   the payload of the message to publish
     * @throws MqttException What happens when we fail
     */
    private void publish(String topic, int qos, byte[] payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);

        // Publish the message
        m_client.publish(topic, message);
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

    @Override
    public void connectionLost(Throwable cause) {
        Log.i(TAG, "connectionLost : " + cause);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        if (s.equals(Constants.TEMPERATURE_TOPIC)) {
            Double value = Double.valueOf(new String(mqttMessage.getPayload()));
            Intent msg = new Intent("temperature");
            msg.putExtra("ACTION", value);
            LocalBroadcastManager.getInstance(this).sendBroadcast(msg);
        }
        if (s.equals(Constants.WATERLEVEL_TOPIC)) {
            Integer value = Integer.valueOf(new String(mqttMessage.getPayload()));
            Intent msg = new Intent("waterlevel");
            msg.putExtra("ACTION", value);
            LocalBroadcastManager.getInstance(this).sendBroadcast(msg);
        }
        if (s.equals(Constants.CONTROLS_TOPIC)) {
            Intent msg = new Intent("controlstate");
            msg.putExtra("ACTION", new String(mqttMessage.getPayload()));
            LocalBroadcastManager.getInstance(this).sendBroadcast(msg);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
    {
        Log.d(TAG, "delivery complete");
    }

    public boolean isConnected() {
        return m_client != null && m_client.isConnected();
    }

    private void sendHeartBeat()
    {
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

    private BroadcastReceiver databaseRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
        }
    };

    public class MyLocalBinder extends Binder {
        AquariumReceiverService getService() {
            return AquariumReceiverService.this;
        }
    }
}