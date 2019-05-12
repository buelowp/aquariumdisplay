package com.home.pete.aquarium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

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
    Handler m_exitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        m_textViewTemperature = findViewById(R.id.textViewTemperatureData);
        m_textViewWaterLevel = findViewById(R.id.textViewWaterLevelData);

        LocalBroadcastManager.getInstance(this).registerReceiver(temperatureUpdate, new IntentFilter("temperature"));
        LocalBroadcastManager.getInstance(this).registerReceiver(waterlevelUpdate, new IntentFilter("waterlevel"));

        m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
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
            Double value = intent.getDoubleExtra("ACTION", 0.0);
            m_textViewTemperature.setText(value.toString() + " \u2109");
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
