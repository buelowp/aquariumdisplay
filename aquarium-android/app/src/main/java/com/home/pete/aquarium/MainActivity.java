package com.home.pete.aquarium;

import android.app.Activity;
import android.app.AlarmManager;
import android.os.Bundle;
import android.provider.Settings;
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

    private Intent m_settings;
    private Intent m_webview;
    private Intent m_lights;
    private MicroCom m_teensy = new MicroCom(this);
    private SunLights m_sunlights= new SunLights(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate()");

        LocalBroadcastManager.getInstance(this).registerReceiver(m_tempLeftReceiver, new IntentFilter("teensy-event-temp-left"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_tempRightReceiver, new IntentFilter("teensy-event-temp-right"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_waterLevelReceiver, new IntentFilter("teensy-event-waterlevel"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_pumpStateReceiver, new IntentFilter("teensy-event-pumpstate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_heaterStateReceiver, new IntentFilter("teensy-event-heaterstate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_toggleUVStateReceiver, new IntentFilter("teensy-event-uvstate"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_helloReceiver, new IntentFilter("teensy-event-hello"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_settingsReceiver, new IntentFilter("settings-event"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_teensyReceiver, new IntentFilter("teensy-event"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_brightnessReceiver, new IntentFilter("teensy-event-brightness"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_primaryLightsReceiver, new IntentFilter("teensy-event-primary-light-state"));

        m_settings = new Intent(this, SettingsActivity.class);
        m_webview = new Intent(this, WebviewActivity.class);
        m_lights = new Intent(this, LightsActivity.class);

        if (!m_teensy.m_helloReceived)
            m_teensy.sendHello();

        AlarmManager am = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
        am.setTimeZone("America/Chicago");
//        Settings.Global.putInt(getContentResolver(), Settings.Global.SCREEN_OFF_TIMEOUT, 60000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        m_sunlights.startOperation();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_sunlights.endOperation();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private BroadcastReceiver m_teensyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            byte msg[] = intent.getByteArrayExtra("ACTION");
            Log.d(TAG, "Lights is asking for: " + Arrays.toString(msg));
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
            Log.d(TAG, "Got hello reponse of: " + msg);
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

    private BroadcastReceiver m_toggleUVStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean value = intent.getBooleanExtra("ACTION", false);
            Log.d(TAG, "Toggling UV state");
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
            Log.d(TAG, "Updating LED brightness");
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
}
