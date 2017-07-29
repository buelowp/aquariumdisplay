package com.home.pete.aquarium;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;

public class LightsActivity extends Activity {
    private static final String TAG = LightsActivity.class.getSimpleName();
    private static final double LATITUDE = 42.058102;
    private static final double LONGITUDE = 87.984189;
    private static final int VIEW_TIMEOUT = 1000 * 60;

    private Handler handler = new Handler();
    private GestureDetector m_gd;
    private Sunposition m_sun = new Sunposition(LATITUDE, LONGITUDE, -5);
    private SunLights m_sunlights = new SunLights(this);

    private ToggleButton tbUVState;
    private ToggleButton tbAllLights;
    private ToggleButton tbSunLights;
    private ToggleButton tbCloudy;
    private SeekBar m_sbBrightness;
    private Context m_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);

        m_gd = new GestureDetector(this, new LightsActivity.MyGestureListener());
        m_context = this;

        Calendar date = Calendar.getInstance();
        m_sun.setCurrentDate(date.get(date.YEAR), date.get(date.MONTH) + 1, date.get(date.DAY_OF_MONTH));

        LocalBroadcastManager.getInstance(this).registerReceiver(m_uvStateReceiver, new IntentFilter("uv-state"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_ledBrightnessReceiver, new IntentFilter("led-brightness"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_primaryLightsStateReceiver, new IntentFilter("led-state"));
        LocalBroadcastManager.getInstance(this).registerReceiver(m_rgbValueReceiver, new IntentFilter("rgb-state"));

        tbUVState = (ToggleButton)findViewById(R.id.toggleButton_UVLights);
        tbAllLights = (ToggleButton)findViewById(R.id.toggleButton_allLights);
        tbSunLights = (ToggleButton)findViewById(R.id.toggleButton_dayLights);
        tbCloudy = (ToggleButton)findViewById(R.id.toggleButton_cloudy);

        m_sbBrightness = (SeekBar)findViewById(R.id.seekBar_brightness);
        m_sbBrightness.setMax(255);
        m_sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                MessagePayload msg = new MessagePayload();
                msg.setBrightness((byte)progressChanged);
                msg.makeFinal();

                Intent i = new Intent("teensy-event");
                i.putExtra("ACTION", msg.getMessage());
                LocalBroadcastManager.getInstance(m_context).sendBroadcast(i);

                Log.d(TAG, "Progress is " + progressChanged);
            }
        });

        getInitialData();
        m_sun.setTZOffset(m_sunlights.getTZOffset());
        Log.d(TAG, "onCreate()");
    }

    private void getInitialData() {
        MessagePayload msg = new MessagePayload();
        msg.getUVState();
        msg.getBrightness();
        msg.getSunLight();
        msg.getRGBValue();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    Runnable finalizer = new Runnable()
    {
        public void run()
        {
            Log.d(TAG, "Activity timed out");
            finish();
        }
    };

    public void setToCloudy(View view) {
        handler.removeCallbacks(finalizer);
        tbCloudy.setChecked(m_sunlights.toggleCloudy());
        handler.postDelayed(finalizer, VIEW_TIMEOUT);
    }

    public void toggleUV(View view) {
        handler.removeCallbacks(finalizer);
        MessagePayload msg = new MessagePayload();
        msg.toggleUVState();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        handler.postDelayed(finalizer, VIEW_TIMEOUT);
    }

    public void toggleSunLights(View view) {
        MessagePayload msg = new MessagePayload();
        msg.toggleSunLights();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void toggleAllLights(View view) {
        MessagePayload msg = new MessagePayload();
        msg.toggleAllLights();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void setToBrightSunlight(View view) {
        MessagePayload msg = new MessagePayload();
        msg.setColor((byte)0xFF, (byte)0xFF, (byte)0xFF);
        msg.setBrightness((byte)0xF0);
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void setToMoonLight(View view) {
        MessagePayload msg = new MessagePayload();
        Calendar date = Calendar.getInstance();
        m_sun.setCurrentDate(date.get(date.YEAR), date.get(date.MONTH) + 1, date.get(date.DAY_OF_MONTH));
        m_sun.setTZOffset(-5);

        msg.setColor((byte)0x00, (byte)0xBF, (byte)0xFF);
        msg.setBrightness((byte)m_sun.moonPhase(date.getTimeInMillis()));
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void setToCloudyLight(View view) {
        MessagePayload msg = new MessagePayload();
        msg.setColor((byte)0xFF, (byte)0xFF, (byte)0xFF);
        msg.setBrightness((byte)0x96);
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void setToSunset(View view) {
        MessagePayload msg = new MessagePayload();
        msg.setColor((byte)0xFF, (byte)0x7F, (byte)0x50);
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    public void restartLightProgram(View view) {
        Intent i = new Intent("teensy-event");
        i.putExtra("RESETLIGHTPROG", 1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private BroadcastReceiver m_ledBrightnessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int value = intent.getIntExtra("ACTION", 0);
            Log.d(TAG, "LEDs are set to a brightness of " + value);
            m_sbBrightness.setProgress((int)value);
        }
    };

    private BroadcastReceiver m_uvStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean value = intent.getBooleanExtra("ACTION", false);
            Log.d(TAG, "Got a UV Lights state of " + value);
            tbUVState.setChecked(value);
            setAllLightsState();
        }
    };

    private BroadcastReceiver m_primaryLightsStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            boolean value = intent.getBooleanExtra("ACTION", false);
            Log.d(TAG, "Got a Primary Lights state of " + value);
            tbSunLights.setChecked(value);
            setAllLightsState();
        }
    };

    private BroadcastReceiver m_rgbValueReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int[] colors = intent.getIntArrayExtra("ACTION");
            Log.d(TAG, "Got a Primary Lights state of " + colors);
            TextView r = (TextView)findViewById(R.id.textView_Red);
            TextView g = (TextView)findViewById(R.id.textView_Green);
            TextView b = (TextView)findViewById(R.id.textView_Blue);
            try {
                r.setText(getResources().getString(R.string.textview_red) + String.valueOf(colors[0]));
                g.setText(getResources().getString(R.string.textview_green)+ String.valueOf(colors[1]));
                b.setText(getResources().getString(R.string.textview_blue) + String.valueOf(colors[2]));
            }
            catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "RGB array Index error " + e.getMessage());
                r.setText(getResources().getString(R.string.textview_red) + String.valueOf(-1));
                g.setText(getResources().getString(R.string.textview_green)+ String.valueOf(-1));
                b.setText(getResources().getString(R.string.textview_blue) + String.valueOf(-1));
            }
        }
    };

    private void setAllLightsState()
    {
        if (!tbUVState.isChecked() && !tbSunLights.isChecked()) {
            tbAllLights.setChecked(false);
        }
        else
            tbAllLights.setChecked(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.m_gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe left' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if(event2.getX() > event1.getX()){
                finish();
            }
            return true;
        }
    }
}
