package com.home.pete.aquarium;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Calendar;
import java.util.Date;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */

public class DataviewActivity extends Activity {
    private static final String TAG = DataviewActivity.class.getSimpleName();

    private IMqttClient client;

    TextView m_temperature;
    TextView m_level;
    Button m_dateChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataview);

        try {
            client = new MqttClient(Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);
        }
        catch (MqttException e) {
            Log.e(TAG, e.toString());
        }

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        client.setCallback(new SimpleMqttCallBack());

        try {
            client.connect(options);
            client.subscribe("aquarium/#");
        }
        catch (MqttException e) {
            Log.e(TAG, e.toString());
        }

        m_temperature = findViewById(R.id.tempTextView);
        m_level = findViewById(R.id.waterLevelTextView);
        m_dateChange = findViewById(R.id.buttonDateChange);
    }

    @Override
    protected void onDestroy() {
        try {
            client.disconnect();
        }
        catch (MqttException e) {
            Log.e(TAG, e.toString());
        }
        super.onDestroy();
    }

    public class SimpleMqttCallBack implements MqttCallback {
        public void connectionLost(Throwable throwable) {
            System.out.println("Connection to MQTT broker lost!");
        }

        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            if (s.equals("aquarium/temperature")) {
                byte[] p = mqttMessage.getPayload();
                m_temperature.setText(p.toString());
            }
            if (s.equals("aquarium/waterlevel")) {
                byte[] p = mqttMessage.getPayload();
                m_level.setText(p.toString());
            }
        }

        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
        {
            Log.d(TAG, "delivery complete");
        }
    }

    public void setWaterChangeDate(View view)
    {
        Log.d(TAG, "Changing water change date to now");
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        String date = "" + day + "/" + month;
        m_dateChange.setText(date);
        MqttMessage message = new MqttMessage(date.getBytes());
        try {
            client.publish("aquarium/waterchange", message);
        }
        catch (MqttException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void exitView(View view)
    {
        Log.d(TAG, "Closing view");
        finish();
    }
}
