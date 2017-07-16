package com.home.pete.aquarium;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.*;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
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
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double LATITUDE = 42.058102;
    private static final double LONGITUDE = 87.984189;
    public static final String BROADCAST_FILTER = "ManageConection_broadcast_receiver_intent_filter";
    PeripheralManagerService service = new PeripheralManagerService();

    private Intent m_settings;
    private Intent m_webview;
    private Intent m_lights;
    private MicroCom m_teensy = new MicroCom(this);
    private Sunposition m_sun = new Sunposition(LATITUDE, LONGITUDE, -5);
    Timer t;
    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar date = Calendar.getInstance();
        m_sun.setCurrentDate(date.get(date.YEAR), date.get(date.MONTH) + 1, date.get(date.DAY_OF_MONTH));

        LocalBroadcastManager.getInstance(this).registerReceiver(m_tempLeftReceiver, new IntentFilter("teensy-event-temp-left"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_tempRightReceiver, new IntentFilter("teensy-event-temp-right"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_waterLevelReceiver, new IntentFilter("teensy-event-waterlevel"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_pumpStateReceiver, new IntentFilter("teensy-event-pumpstate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_heaterStateReceiver, new IntentFilter("teensy-event-heaterstate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_helloReceiver, new IntentFilter("teensy-event-hello"));
        m_settings = new Intent(this, SettingsActivity.class);
        m_webview = new Intent(this, WebviewActivity.class);
        m_lights = new Intent(this, LightsActivity.class);
        m_teensy.sendHello();
        startDailyTimer();
    }

    public void startDailyTimer() {
        t = new Timer();
        task = new TimerTask() {
            Calendar date = Calendar.getInstance();

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        m_sun.setCurrentDate(date.get(date.YEAR), date.get(date.MONTH) + 1, date.get(date.DAY_OF_MONTH));
                    }
                });
            }
        };
        t.scheduleAtFixedRate(task, 0, 1000 * 60 * 24);
    }

    public void startUIUpdateTimer(){
        t = new Timer();
        task = new TimerTask() {
            TextView temp1 = (TextView)findViewById(R.id.textView_TempRightString);
            TextView temp2 = (TextView)findViewById(R.id.textView_TempLeftString);
            TextView level = (TextView)findViewById(R.id.textView_WaterLevel);

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        level.setText("" + 0);
                    }
                });
            }
        };
        t.scheduleAtFixedRate(task, 0, 1000 * 60);
    }

    private BroadcastReceiver m_helloReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int msg = intent.getIntExtra("ACTION", 0);
            Log.d(TAG, "Got hello reponse of: " + msg);
        }
    };

    private BroadcastReceiver m_tempLeftReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float msg = intent.getFloatExtra("ACTION", (float)0.0);
            Log.d(TAG, "Got left temp: " + msg);
        }
    };

    private BroadcastReceiver m_tempRightReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float msg = intent.getFloatExtra("ACTION", (float)0.0);
            Log.d(TAG, "Got right temp: " + msg);
        }
    };

    private BroadcastReceiver m_waterLevelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float msg = intent.getFloatExtra("ACTION", (float)0.0);
            Log.d(TAG, "Got water level: " + msg);
        }
    };

    private BroadcastReceiver m_pumpStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int msg = intent.getIntExtra("ACTION", 0);
            Log.d(TAG, "Got pump state: " + msg);
        }
    };

    private BroadcastReceiver m_heaterStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int msg = intent.getIntExtra("ACTION", 0);
            Log.d(TAG, "Got heater state: " + msg);
        }
    };

    public void viewFish(View view) {
        Log.d(TAG, "Viewing my fish");
        setContentView(R.layout.activity_webview);
        startActivity(m_webview);
    }

    public void viewSettings(View view) {
        Log.d(TAG, "Viewing settings");
        setContentView(R.layout.activity_settings);

        if (m_teensy.m_helloReceived)
            m_teensy.requestWaterLevel();

        startActivity(m_settings);
    }

    public void viewLights(View view) {
        Log.d(TAG, "Managing the lights");
        setContentView(R.layout.activity_lights);
        startActivity(m_lights);
    }
}
