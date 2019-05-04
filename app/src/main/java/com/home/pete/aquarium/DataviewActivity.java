package com.home.pete.aquarium;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

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

public class DataviewActivity extends Activity {
    private static final String TAG = DataviewActivity.class.getSimpleName();

    TextView m_temperature;
    TextView m_level;
    Button m_dateChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataview);

        m_temperature = findViewById(R.id.tempValue);
        m_level = findViewById(R.id.levelValue);
        m_dateChange = findViewById(R.id.buttonDateChange);
        m_temperature.setText("Other");
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    public void setWaterChangeDate(View view)
    {
        Log.d(TAG, "Changing water change date to now");
    }

    public void exitView(View view)
    {
        Log.d(TAG, "Closing view");
        finish();
    }
}
