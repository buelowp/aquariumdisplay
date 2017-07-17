package com.home.pete.aquarium;

import android.os.Bundle;
import android.app.Activity;

import com.google.android.things.pio.*;
import android.content.*;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ToggleButton;


public class SettingsActivity extends Activity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final double LATITUDE = 42.058102;
    private static final double LONGITUDE = 87.984189;
    PeripheralManagerService service = new PeripheralManagerService();

    private GestureDetector m_gd;
    private Timer t;
    private TimerTask task;
    private Sunposition m_sun = new Sunposition(LATITUDE, LONGITUDE, -5);
    TextView m_leftTemp;
    TextView m_rightTemp;
    TextView m_waterLevel;
    TextView m_sunrise;
    TextView m_sunset;
    TextView m_moonphase;
    ToggleButton m_pumpState;
    ToggleButton m_heaterState;
    ToggleButton m_systemState;
    Button m_waterChange;
    Button m_filterChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        m_gd = new GestureDetector(this, new SettingsActivity.MyGestureListener());
        Calendar date = Calendar.getInstance();
        m_sun.setCurrentDate(date.get(date.YEAR), date.get(date.MONTH) + 1, date.get(date.DAY_OF_MONTH));
        m_sun.setTZOffset(-5);

        m_leftTemp = (TextView) findViewById(R.id.textView_TempLeft);
        m_rightTemp = (TextView) findViewById(R.id.textView_TempRight);
        m_waterLevel = (TextView) findViewById(R.id.textView_WaterLevel);
        m_sunrise = (TextView) findViewById(R.id.textView_sunrise_value);
        m_sunset = (TextView) findViewById(R.id.textView_sunset_value);
        m_moonphase = (TextView) findViewById(R.id.textView_moonphase_value);
        m_pumpState = (ToggleButton)findViewById(R.id.toggle_PumpState);
        m_heaterState = (ToggleButton)findViewById(R.id.toggle_HeaterState);
        m_systemState = (ToggleButton)findViewById(R.id.toggle_LightState);
        m_waterChange = (Button)findViewById(R.id.button_ChangeWater);
        m_filterChange = (Button)findViewById(R.id.button_changeFilter);

        LocalBroadcastManager.getInstance(this).registerReceiver(m_heaterStateMessage, new IntentFilter("heater-state"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_pumpStateMessage, new IntentFilter("pump-state"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_leftTempMessage, new IntentFilter("left-temp"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_rightTempMessage, new IntentFilter("right-temp"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_waterLevelMessage, new IntentFilter("water-level"));
        setCelestialBodies();
        getInitialData();
    }

    public void startUIUpdateTimer() {
        t = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
            };
        };
        t.scheduleAtFixedRate(task, 0, 1000 * 60);
    }

    private BroadcastReceiver m_heaterStateMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean value = intent.getBooleanExtra("ACTION", false);
            m_heaterState.setChecked(value);
        }
    };

    private BroadcastReceiver m_pumpStateMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean value = intent.getBooleanExtra("ACTION", false);
            m_pumpState.setChecked(value);
        }
    };

    private BroadcastReceiver m_leftTempMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float value = intent.getFloatExtra("ACTION", 0);
            m_leftTemp.setText("" + value);
        }
    };

    private BroadcastReceiver m_rightTempMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float value = intent.getFloatExtra("ACTION", 0);
            m_rightTemp.setText("" + value);
        }
    };

    private BroadcastReceiver m_waterLevelMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
        }
    };

    private void setCelestialBodies()
    {
        Calendar date = Calendar.getInstance();
        double sunrise = m_sun.calcSunrise();
        double sunset = m_sun.calcSunset();
        int moonphase = m_sun.moonPhase(date.getTimeInMillis());
        int sunriseHour = (int)sunrise / 60;
        int sunriseMin = (int)sunrise % 60;
        int sunsetHour = (int)sunset / 60;
        int sunsetMin = (int)sunset % 60;

        m_moonphase.setText(String.valueOf(moonphase));
        m_sunrise.setText("" + sunriseHour + ":" + sunriseMin + " AM");
        m_sunset.setText("" + sunsetHour + ":" + sunsetMin + " PM");

        TimeZone tz = date.getTimeZone();

        // print the time zone name for this calendar
        Log.d(TAG, "The time zone is: " + tz.getDisplayName());
        Log.d(TAG, "Timezone offset is: " + tz.getRawOffset());

    }

    private void getInitialData()
    {
        MessagePayload msg = new MessagePayload();
        msg.getHeaterState();
        msg.getPumpState();
        msg.getTemps();
//        msg.getWaterLevel();
        msg.makeFinal();

        Intent i = new Intent("settings-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void changeWater(View view)
    {

    }

    public void changeFilter(View view)
    {

    }

    public void togglePump(View view)
    {
        MessagePayload msg = new MessagePayload();
        msg.togglePumpState();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void toggleHeater(View view)
    {
        MessagePayload msg = new MessagePayload();
        msg.toggleHeaterState();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void toggleSystem(View view)
    {
        MessagePayload msg = new MessagePayload();
        msg.powerOffSystem();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.m_gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event2.getX() > event1.getX()){
                //switch another activity
                Intent intent = new Intent(
                        SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }

            return true;
        }
    }

}
