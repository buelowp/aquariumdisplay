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

import java.util.Calendar;

public class LightsActivity extends Activity {
    private static final String TAG = LightsActivity.class.getSimpleName();
    private static final double LATITUDE = 42.058102;
    private static final double LONGITUDE = 87.984189;

    private Handler handler = new Handler();
    private GestureDetector m_gd;
    private boolean m_uvEnabled;
    private boolean m_sunLightOn;
    private Sunposition m_sun = new Sunposition(LATITUDE, LONGITUDE, -5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);

        m_uvEnabled = false;
        m_sunLightOn = false;
        m_gd = new GestureDetector(this, new LightsActivity.MyGestureListener());

        Calendar date = Calendar.getInstance();
        m_sun.setCurrentDate(date.get(date.YEAR), date.get(date.MONTH) + 1, date.get(date.DAY_OF_MONTH));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("lights-event"));
        handler.postDelayed(finalizer, 1000 * 10);
    }

    Runnable finalizer = new Runnable()
    {
        public void run()
        {
            Log.d(TAG, "Activity timed out");
            finish();
        }
    };

    public void toggleUV(View view) {
        MessagePayload msg = new MessagePayload();
        msg.toggleUVState();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.m_gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe left' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event2.getX() > event1.getX()){
                //switch another activity
                Intent intent = new Intent(
                        LightsActivity.this, MainActivity.class);
                startActivity(intent);
            }

            return true;
        }
    }
}
