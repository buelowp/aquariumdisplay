package com.home.pete.aquarium;

import android.os.Bundle;
import android.app.Activity;

import com.google.android.things.pio.*;
import android.content.*;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import android.support.v4.content.LocalBroadcastManager;


public class SettingsActivity extends Activity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final double LATITUDE = 42.058102;
    private static final double LONGITUDE = 87.984189;
    PeripheralManagerService service = new PeripheralManagerService();

    private GestureDetector m_gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        m_gd = new GestureDetector(this, new SettingsActivity.MyGestureListener());

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("teensy-event"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("settings-event"));
        getInitialStates();
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
        }
    };

    private void getInitialStates()
    {
        MessagePayload msg = new MessagePayload();
        msg.getHeaterState();
        msg.getPumpState();
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
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
