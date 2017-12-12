package com.home.pete.aquarium;

import android.os.Bundle;
import android.app.Activity;

import android.content.*;

import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import java.util.TimeZone;
import java.util.Calendar;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ToggleButton;


public class SettingsActivity extends Activity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final double LATITUDE = 42.058102;
    private static final double LONGITUDE = 87.984189;
    private static final int VIEW_TIMEOUT = 1000 * 60;

    private Handler handler = new Handler();
    private GestureDetector m_gd;
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
        m_sun.setCurrentDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
        m_sun.setTZOffset(-5);

        m_leftTemp = findViewById(R.id.textView_TempLeft);
        m_rightTemp = findViewById(R.id.textView_TempRight);
        m_waterLevel = findViewById(R.id.textView_WaterLevel);
        m_sunrise = findViewById(R.id.textView_sunrise_value);
        m_sunset = findViewById(R.id.textView_sunset_value);
        m_moonphase = findViewById(R.id.textView_moonphase_value);
        m_pumpState = findViewById(R.id.toggle_PumpState);
        m_heaterState = findViewById(R.id.toggle_HeaterState);
        m_systemState = findViewById(R.id.toggle_LightState);
        m_waterChange = findViewById(R.id.button_ChangeWater);
        m_filterChange = findViewById(R.id.button_changeFilter);

        LocalBroadcastManager.getInstance(this).registerReceiver(m_heaterStateMessage, new IntentFilter("heater-state"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_pumpStateMessage, new IntentFilter("pump-state"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_leftTempMessage, new IntentFilter("left-temp"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_rightTempMessage, new IntentFilter("right-temp"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_waterLevelMessage, new IntentFilter("water-level"));
        setCelestialBodies();
        getInitialData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(finalizer, VIEW_TIMEOUT);
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(finalizer);
        Log.d(TAG, "onStop");
    }

    Runnable finalizer = new Runnable()
    {
        public void run()
        {
            Log.d(TAG, "Activity timed out");
            finish();
        }
    };

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
            m_leftTemp.setText(String.valueOf(value));
        }
    };

    private BroadcastReceiver m_rightTempMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            float value = intent.getFloatExtra("ACTION", 0);
            m_rightTemp.setText(String.valueOf(value));
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
        int sunriseHour = (int)sunrise / 60;
        int sunriseMin = (int)sunrise % 60;
        int sunsetHour = (int)sunset / 60;
        int sunsetMin = (int)sunset % 60;

        m_moonphase.setText(String.valueOf(m_sun.moonPhase()));
        String sbSunrise = String.valueOf(sunriseHour) +
                ":" +
                sunriseMin +
                " AM";
        m_sunrise.setText(sbSunrise);
        String sbSunset = String.valueOf(sunsetHour) +
                ":" +
                sunsetMin +
                " PM";
        m_sunset.setText(sbSunset);

        TimeZone tz = date.getTimeZone();

        // print the time zone name for this calendar
        Log.d(TAG, "The time zone is: " + tz.getDisplayName());
        Log.d(TAG, "Timezone offset is: " + tz.getRawOffset() / 1000 / 60 / 60);

    }

    private void getInitialData()
    {
        MessagePayload msg = new MessagePayload();
        msg.getHeaterState();
        msg.getPumpState();
        msg.getTemps();
        msg.getWaterLevel();
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
        handler.removeCallbacks(finalizer);
        MessagePayload msg = new MessagePayload();
        msg.togglePumpState();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        handler.postDelayed(finalizer, VIEW_TIMEOUT);
    }

    public void toggleHeater(View view)
    {
        handler.removeCallbacks(finalizer);
        MessagePayload msg = new MessagePayload();
        msg.toggleHeaterState();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        handler.postDelayed(finalizer, VIEW_TIMEOUT);
    }

    public void toggleSystem(View view)
    {
        handler.removeCallbacks(finalizer);
        MessagePayload msg = new MessagePayload();
        msg.powerOffSystem();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        handler.postDelayed(finalizer, VIEW_TIMEOUT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "Handing a touch event");
        handler.removeCallbacks(finalizer);
        handler.postDelayed(finalizer, VIEW_TIMEOUT);
        this.m_gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event2.getX() > event1.getX()){
                //switch another activity
                finish();
            }

            return true;
        }
    }

}
