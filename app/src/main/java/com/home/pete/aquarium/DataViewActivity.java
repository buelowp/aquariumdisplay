package com.home.pete.aquarium;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.home.pete.aquarium.Constants.VIEW_TIMEOUT;

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
public class DataViewActivity extends Activity {
    private static final String TAG = DataViewActivity.class.getSimpleName();

    TextView m_textViewTemperature;
    TextView m_textViewWaterLevel;
    TextView m_textViewIronAddition;
    TextView m_textViewWaterChange;
    TextView m_textViewFoamFilterChange;
    TextView m_textViewBioFoamFilterChange;
    TextView m_textViewClearMaxFilterChange;
    TextView m_textViewPurigenFilterChange;
    TextView m_textViewBloodWormsFed;
    TextView m_textViewFilterChange;
    Handler m_exitHandler = new Handler();

    SharedPreferences m_preferences;

    public static final String PREFERENCES = "aquarium";
    public static final String WATER_CHANGE = "waterchange";
    public static final String FILTER_CHANGE = "filterchange";
    public static final String IRON_ADD = "ironaddition";
    public static final String CLEARMAX_CHANGE = "clearmax";
    public static final String BIOFOAM_CHANGE = "biofoam";
    public static final String FOAM_CHANGE = "foamfilter";
    public static final String PURIGEN_CHANGE = "purigen";
    public static final String BLOOD_WORMS = "bloodworms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_data_view);

        m_preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        m_textViewTemperature = findViewById(R.id.textViewTemperatureData);
        m_textViewWaterLevel = findViewById(R.id.textViewWaterLevelData);
        m_textViewIronAddition = findViewById(R.id.textViewIronAdditionDateData);
        m_textViewWaterChange = findViewById(R.id.textViewWaterChangeDateData);
        m_textViewPurigenFilterChange = findViewById(R.id.textViewPurigenChangeDateData);
        m_textViewBioFoamFilterChange = findViewById(R.id.textViewBioFoamFilterDateData);
        m_textViewFoamFilterChange = findViewById(R.id.textViewFoamFilterDateData);
        m_textViewClearMaxFilterChange = findViewById(R.id.textViewClearMaxChangeDateData);
        m_textViewBloodWormsFed = findViewById(R.id.textViewWormFoodDateData);
        m_textViewFilterChange = findViewById(R.id.textViewFilterChangeDateData);

        m_textViewWaterChange.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    SharedPreferences.Editor e = m_preferences.edit();
                    e.putLong(WATER_CHANGE, time);
                    e.commit();
                    Log.i(TAG, "Stored " + time + " to shared preferences for last water change");
                    updateWaterChangeDate();
                    m_exitHandler.removeCallbacks(exitViewOnTimeout);
                    m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
                }

                return false;
            }
        });

        m_textViewIronAddition.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    SharedPreferences.Editor e = m_preferences.edit();
                    e.putLong(IRON_ADD, time);
                    e.commit();
                    Log.i(TAG, "Stored " + time + " to shared preferences for last iron addition");
                    updateIronAdditionDate();
                    m_exitHandler.removeCallbacks(exitViewOnTimeout);
                    m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
                }

                return false;
            }
        });

        m_textViewFilterChange.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    SharedPreferences.Editor e = m_preferences.edit();
                    e.putLong(FILTER_CHANGE, time);
                    e.commit();
                    Log.i(TAG, "Stored " + time + " to shared preferences for last filter change");
                    updateFilterChangeDate();
                    m_exitHandler.removeCallbacks(exitViewOnTimeout);
                    m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
                }

                return false;
            }
        });

        m_textViewClearMaxFilterChange.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    SharedPreferences.Editor e = m_preferences.edit();
                    e.putLong(CLEARMAX_CHANGE, time);
                    e.commit();
                    Log.i(TAG, "Stored " + time + " to shared preferences for last clearmax change");
                    updateClearmaxFilterDate();
                    m_exitHandler.removeCallbacks(exitViewOnTimeout);
                    m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
                }

                return false;
            }
        });

        m_textViewFoamFilterChange.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    SharedPreferences.Editor e = m_preferences.edit();
                    e.putLong(FOAM_CHANGE, time);
                    e.commit();
                    Log.i(TAG, "Stored " + time + " to shared preferences for last foam filter change");
                    updateFoamFilterDate();
                    m_exitHandler.removeCallbacks(exitViewOnTimeout);
                    m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
                }

                return false;
            }
        });

        m_textViewPurigenFilterChange.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    SharedPreferences.Editor e = m_preferences.edit();
                    e.putLong(PURIGEN_CHANGE, time);
                    e.commit();
                    Log.i(TAG, "Stored " + time + " to shared preferences for last clearmax filter change");
                    updatePurigenFilterDate();
                    m_exitHandler.removeCallbacks(exitViewOnTimeout);
                    m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
                }

                return false;
            }
        });

        m_textViewBioFoamFilterChange.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    SharedPreferences.Editor e = m_preferences.edit();
                    e.putLong(BIOFOAM_CHANGE, time);
                    e.commit();
                    Log.i(TAG, "Stored " + time + " to shared preferences for last bio foam change");
                    updateBioFoamFilterDate();
                    m_exitHandler.removeCallbacks(exitViewOnTimeout);
                    m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
                }

                return false;
            }
        });

        m_textViewBloodWormsFed.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    SharedPreferences.Editor e = m_preferences.edit();
                    e.putLong(BLOOD_WORMS, time);
                    e.commit();
                    Log.i(TAG, "Stored " + time + " to shared preferences for last blood worms fed");
                    updateBloorWormsDate();
                    m_exitHandler.removeCallbacks(exitViewOnTimeout);
                    m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
                }

                return false;
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(temperatureUpdate, new IntentFilter("temperature"));
        LocalBroadcastManager.getInstance(this).registerReceiver(waterlevelUpdate, new IntentFilter("waterlevel"));

        updateFilterChangeDate();
        updateIronAdditionDate();
        updateWaterChangeDate();
        updateBloorWormsDate();
        updateBioFoamFilterDate();
        updateClearmaxFilterDate();
        updateFoamFilterDate();
        updatePurigenFilterDate();

        m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void updateFilterChangeDate()
    {
        Calendar c = Calendar.getInstance();
        long item = m_preferences.getLong(FILTER_CHANGE, 0);
        c.setTimeInMillis(item);
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d");
        m_textViewFilterChange.setText(df.format(c.getTime()));
    }

    private void updateWaterChangeDate()
    {
        Calendar c = Calendar.getInstance();
        long item = m_preferences.getLong(WATER_CHANGE, 0);
        Log.d(TAG, "Got " + item + " for last water change millis");
        c.setTimeInMillis(item);
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d");
        m_textViewWaterChange.setText(df.format(c.getTime()));
    }

    private void updateBioFoamFilterDate()
    {
        Calendar c = Calendar.getInstance();
        long item = m_preferences.getLong(BIOFOAM_CHANGE, 0);
        c.setTimeInMillis(item);
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d");
        m_textViewIronAddition.setText(df.format(c.getTime()));
    }

    private void updatePurigenFilterDate()
    {
        Calendar c = Calendar.getInstance();
        long item = m_preferences.getLong(PURIGEN_CHANGE, 0);
        c.setTimeInMillis(item);
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d");
        m_textViewIronAddition.setText(df.format(c.getTime()));
    }
    private void updateClearmaxFilterDate()
    {
        Calendar c = Calendar.getInstance();
        long item = m_preferences.getLong(CLEARMAX_CHANGE, 0);
        c.setTimeInMillis(item);
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d");
        m_textViewIronAddition.setText(df.format(c.getTime()));
    }
    private void updateBloorWormsDate()
    {
        Calendar c = Calendar.getInstance();
        long item = m_preferences.getLong(BLOOD_WORMS, 0);
        c.setTimeInMillis(item);
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d");
        m_textViewIronAddition.setText(df.format(c.getTime()));
    }
    private void updateFoamFilterDate()
    {
        Calendar c = Calendar.getInstance();
        long item = m_preferences.getLong(FOAM_CHANGE, 0);
        c.setTimeInMillis(item);
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d");
        m_textViewIronAddition.setText(df.format(c.getTime()));
    }

    private void updateIronAdditionDate()
    {
        Calendar c = Calendar.getInstance();
        long item = m_preferences.getLong(IRON_ADD, 0);
        Log.d(TAG, "Got " + item + " for last water change millis");
        c.setTimeInMillis(item);
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d");
        m_textViewWaterChange.setText(df.format(c.getTime()));
    }

    public void exitView(View view)
    {
        Log.d(TAG, "Closing view");
        m_exitHandler.removeCallbacks(exitViewOnTimeout);
        finish();
    }

    private BroadcastReceiver temperatureUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            double value = intent.getDoubleExtra("ACTION", 0.0);
            String temp = String.format("%.1f%s", value, "\u2109");
            m_textViewTemperature.setText(temp);
        }
    };

    private BroadcastReceiver waterlevelUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Integer value = intent.getIntExtra("ACTION", 0);
            String text;
            if (value > 2760)
                text = "full";
            else if (value > 2750)
                text = "-1 cm";
            else if (value > 2730)
                text = "-2 cm";
            else
                text = value.toString();

            m_textViewWaterLevel.setText(text);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    private Runnable exitViewOnTimeout = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Closing view due to timeout handler");
            finish();
        }
    };
}
