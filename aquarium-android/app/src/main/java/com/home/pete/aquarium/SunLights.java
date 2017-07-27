package com.home.pete.aquarium;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.TimeZone;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.sql.Time;
import java.util.Random;

/**
 * Created by pete on 7/21/17.
 */

public class SunLights {
    private static final String TAG = SunLights.class.getSimpleName();
    private static final double LATITUDE = 42.058102;
    private static final double LONGITUDE = 87.984189;
    private static final int UPDATE_TIME = 1000 * 60;
    private Sunposition m_sun = new Sunposition(LATITUDE, LONGITUDE, -5);
    private Context m_context;
    private Handler m_handler = new Handler();
    boolean m_uvState;
    boolean m_cloudy;
    int m_lastBrightness;

    SunLights(Context context)
    {
        Log.d(TAG, "Sunlights Constructor");
        m_context = context;
        setDate();
        m_uvState = false;
        m_cloudy = false;
        m_lastBrightness = 0;
    }

    public void endOperation()
    {
        m_handler.removeCallbacks(periodicUpdate);
        Log.d(TAG, "Ending lights program");
    }

    public void startOperation()
    {
        m_sun.setTZOffset(getTZOffset());
        m_handler.postDelayed(periodicUpdate, UPDATE_TIME);
        Log.d(TAG, "Starting lights program");
    }

    public int getTZOffset()
    {
        int raw = ((TimeZone.getDefault().getRawOffset() / 1000) / 60) / 60;
        int dst = ((TimeZone.getDefault().getDSTSavings() / 1000) / 60) / 60;

        Log.d(TAG, "Current numeric offset is " + (raw + dst));
        return raw + dst;
    }

    private int isSunrise()
    {
        double sunup = m_sun.calcSunrise() - 30;
        Calendar calendar = new GregorianCalendar();
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        int rval;

        if (sunup < 330)  // Avoid starting earlier than 6 am
            sunup = 330;

        rval = now - (int)sunup;
        if (rval > 60)
            rval = -1;
        else if (rval < 0)
            rval = -1;

        Log.d(TAG, "Sunrise return value is " + rval + " and sunrise is " + sunup + " and now is " + now);
        return rval;
    }

    private int isSunset()
    {
        double sundown = m_sun.calcSunset() + 30;
        Calendar calendar = new GregorianCalendar();
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        int rval;

        if (sundown > 1230)  // Avoid ending later than 8:30
            sundown = 1230;

        rval = (int)sundown - now;
        if (rval > 60)
            rval = -1;
        else if (rval < 0)
            rval = -1;

        Log.d(TAG, "Sundown return value is " + rval + " and sundown is " + sundown + " and now is " + now);
        return rval;
    }

    private void setLEDColor(int r, int g, int b)
    {
        MessagePayload msg = new MessagePayload();
        msg.setColor((byte)(r & 0xFF), (byte)(g & 0xFF), (byte)(b & 0xFF));
        msg.makeFinal();
        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(m_context).sendBroadcast(i);
    }

    private void setLEDBrightness(int value)
    {
        MessagePayload msg = new MessagePayload();
        msg.setBrightness((byte)(value & 0xFF));
        msg.makeFinal();

        Intent i = new Intent("teensy-event");
        i.putExtra("ACTION", msg.getMessage());
        LocalBroadcastManager.getInstance(m_context).sendBroadcast(i);
    }

    private int getCalculatedSunrise()
    {
        int sunup = (int)m_sun.calcSunrise();
        if (sunup < 390)  // Avoid starting earlier than 6 am
            sunup = 390;

        return sunup;
    }

    private int getCalculatedSunset()
    {
        int sundown = (int)m_sun.calcSunset();
        if (sundown > 1170)  // Avoid ending later than 8:30
            sundown = 1170;

        return sundown;
    }

    private boolean isDaytime()
    {
        Calendar calendar = new GregorianCalendar();
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        int sunup = getCalculatedSunrise();
        int sundown = getCalculatedSunset();

        if ((now > sunup) && (now < sundown)) {
            Log.d(TAG, "It's daytime");
            return true;
        }
        Log.d(TAG, "It's nighttime");
        return false;
    }

    private void setDate()
    {
        Calendar calendar = new GregorianCalendar();
        int month = calendar.get(calendar.MONTH) + 1;

        m_sun.setCurrentDate(calendar.get(calendar.YEAR),  month, calendar.get(calendar.DAY_OF_MONTH));
        m_sun.setTZOffset(getTZOffset());
    }

    private boolean isUVTime()
    {
        Calendar calendar = new GregorianCalendar();
        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

        if (now >= 1140 && now <= 1320)
            return true;

        return false;
    }

    public boolean toggleCloudy()
    {
        m_cloudy = !m_cloudy;
        return m_cloudy;
    }

    private void turnOnUV()
    {

    }

    Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            int sunrise = isSunrise();
            int sunset = isSunset();

            setDate();

            try {
                if ((sunrise > 0) && !isDaytime()) {
                    Log.d(TAG, "Running sunrise with offset " + sunrise);
                    setLEDBrightness(sunrise * 3);
                    m_lastBrightness = sunrise * 3;
                }
                else if (isDaytime()) {
                    Log.d(TAG, "Running daytime");
                    if (!m_cloudy) {
                        setLEDBrightness(180);
                        m_lastBrightness = 180;
                    }
                    else {
                        Random rand = new Random();
                        int value = rand.nextInt(2);
                        if (value == 0) {
                            value = -3;
                        }
                        else {
                            value = 3;
                        }
                        if (m_lastBrightness == 120)
                            value = 3;
                        else if (m_lastBrightness == 150)
                            value = -3;

                        setLEDBrightness(m_lastBrightness + value);
                        m_lastBrightness += value;
                    }
                }
                else if ((sunset > 0) && !isDaytime()) {
                    Log.d(TAG, "Running sunset with offset " + sunset);
                    setLEDBrightness(180 - (sunset * 3));
                    m_lastBrightness = 180 - (sunset * 3);
                }
                else {
                    int moonphase = (int)m_sun.moonPhase();
                    Log.d(TAG, "raw moonphase is " + moonphase);
                    if (moonphase > 14) {
                        moonphase = 29 - moonphase;
                    }
                    Log.d(TAG, "the moon is at position " + moonphase);
                    setLEDBrightness((byte)((int)moonphase & 0xFF));
                }

                if (isUVTime()) {
                    turnOnUV();
                }
            }
            finally {
                m_handler.postDelayed(periodicUpdate, UPDATE_TIME);
            }
        }
    };
}
