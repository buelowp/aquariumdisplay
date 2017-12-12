package com.home.pete.aquarium;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.content.*;

import com.google.android.things.device.TimeManager;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Handler;
import java.util.Arrays;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;


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

    private Intent m_settings;
    private Intent m_webview;
    private Intent m_lights;
    private MicroCom m_teensy;

    TimeManager m_timeManager;
    Sunposition m_sun = new Sunposition(LATITUDE, LONGITUDE, -5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");
        final Handler m_updateLighting;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Log.d(TAG, "X = " + size.x);
        Log.d(TAG, "Y = " + size.y);

        LocalBroadcastManager.getInstance(this).registerReceiver(m_tempLeftReceiver, new IntentFilter("teensy-event-temp-left"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_tempRightReceiver, new IntentFilter("teensy-event-temp-right"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_waterLevelReceiver, new IntentFilter("teensy-event-waterlevel"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_pumpStateReceiver, new IntentFilter("teensy-event-pumpstate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_heaterStateReceiver, new IntentFilter("teensy-event-heaterstate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_UVStateReceiver, new IntentFilter("teensy-event-uvstate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_helloReceiver, new IntentFilter("teensy-event-hello"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_settingsReceiver, new IntentFilter("settings-event"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_teensyReceiver, new IntentFilter("teensy-event"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_brightnessReceiver, new IntentFilter("teensy-event-brightness"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_primaryLightsReceiver, new IntentFilter("teensy-event-primary-light-state"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_rgbValueReceiver, new IntentFilter("teensy-event-rgb"));

        m_settings = new Intent(this, SettingsActivity.class);
        m_webview = new Intent(this, WebviewActivity.class);
        m_lights = new Intent(this, LightsActivity.class);
        m_teensy = new MicroCom(this);
        m_timeManager = new TimeManager();

        try {
            m_timeManager.setAutoTimeEnabled(true);
            m_timeManager.setTimeZone("America/Chicago");
        }
        catch (RuntimeException e) {
            Log.e(TAG, "Time service not available: " + e.getMessage());
            e.printStackTrace(System.err);
        }

        m_updateLighting = new Handler();
        m_updateLighting.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Calendar date = Calendar.getInstance();
                TimeZone tz = date.getTimeZone();
                m_sun.setCurrentDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
                m_sun.setTZOffset(tz.getRawOffset() / 1000 / 60 / 60);
                Log.d(TAG, "Setting timezone to: " + tz.getRawOffset() / 1000 / 60 / 60);
                m_updateLighting.postDelayed(this, 1000 * 60);
            }
        }, 1000 * 60);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "onDestroy");
//        m_sunlights.endOperation();
    }

    private BroadcastReceiver m_teensyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            byte msg[] = intent.getByteArrayExtra("ACTION");
//            Log.d(TAG, "System is asking teensy to do: " + Arrays.toString(msg));
            m_teensy.sendPreformattedMsg(msg);
        }
    };

    private BroadcastReceiver m_settingsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            byte msg[] = intent.getByteArrayExtra("ACTION");
            Log.d(TAG, "Settings is asking for: " + Arrays.toString(msg));
            m_teensy.sendPreformattedMsg(msg);
        }
    };

    private BroadcastReceiver m_helloReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int msg = intent.getIntExtra("ACTION", 0);
//            Log.d(TAG, "Got hello reponse of: " + msg);
        }
    };

    private BroadcastReceiver m_tempLeftReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float value = intent.getFloatExtra("ACTION", 0);
            Intent msg = new Intent("left-temp");
            msg.putExtra("ACTION", value);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
            Log.d(TAG, "Got left temp: " + msg);
        }
    };

    private BroadcastReceiver m_tempRightReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float value = intent.getFloatExtra("ACTION", 0);
            Intent msg = new Intent("right-temp");
            msg.putExtra("ACTION", value);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
            Log.d(TAG, "Got right temp: " + msg);
        }
    };

    private BroadcastReceiver m_waterLevelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float value = intent.getFloatExtra("ACTION", (float)0.0);
            Intent msg = new Intent("water-level");
            msg.putExtra("ACTION", value);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
            Log.d(TAG, "Got water level: " + msg);
        }
    };

    private BroadcastReceiver m_pumpStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean value = intent.getBooleanExtra("ACTION", false);
            Intent msg = new Intent("pump-state");
            msg.putExtra("ACTION", value);
            Log.d(TAG, "Got pump state: " + value);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
        }
    };

    private BroadcastReceiver m_heaterStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean value = intent.getBooleanExtra("ACTION", false);
            Intent msg = new Intent("pump-state");
            msg.putExtra("ACTION", value);
            Log.d(TAG, "Got heater state: " + value);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
        }
    };

    private BroadcastReceiver m_UVStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean value = intent.getBooleanExtra("ACTION", false);
//            Log.d(TAG, "Got a UV state of " + value);
            Intent msg = new Intent("uv-state");
            msg.putExtra("ACTION", value);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
        }
    };

    private BroadcastReceiver m_brightnessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int value = intent.getIntExtra("ACTION", 0);
            Log.d(TAG, "Updating LED brightness to " + value);
            Intent msg = new Intent("led-brightness");
            msg.putExtra("ACTION", value);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
        }
    };

    private BroadcastReceiver m_primaryLightsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean value = intent.getBooleanExtra("ACTION", false);
            Log.d(TAG, "Updating primary light state");
            Intent msg = new Intent("led-state");
            msg.putExtra("ACTION", value);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
        }
    };

    private BroadcastReceiver m_rgbValueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int[] value = intent.getIntArrayExtra("ACTION");
            Log.d(TAG, "Updating RGB values");
            Intent msg = new Intent("rgb-state");
            msg.putExtra("ACTION", value);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg);
        }
    };

    public void viewFish(View view) {
        Log.d(TAG, "Viewing my fish");
        startActivity(m_webview);
    }

    public void viewSettings(View view) {
        Log.d(TAG, "Viewing settings");
        startActivity(m_settings);
    }

    public void viewLights(View view) {
        Log.d(TAG, "Managing the lights");
        startActivity(m_lights);
    }

    private boolean isDaytime()
    {
        double sunrise = m_sun.calcSunrise();
        Calendar now = Calendar.getInstance();
        long minsPastMidnight = now.getTimeInMillis() / 1000 / 60;

        return false;
    }

    private boolean isSunrise() {
        double sunrise = m_sun.calcSunrise();
        Calendar now = Calendar.getInstance();
        long minsPastMidnight = now.getTimeInMillis() / 1000 / 60;
        return false;
    }
}
