package com.home.pete.aquarium;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Intent m_webviewIntent;
    private Intent m_dataviewIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_webviewIntent = new Intent(this, WebviewActivity.class);
        m_dataviewIntent = new Intent(this, DataviewActivity.class);
    }

    public void viewData(View view)
    {
        Log.d(TAG, "Viewing settings");
        startActivity(m_dataviewIntent);
    }

    public void viewFish(View view)
    {
        Log.d(TAG, "Viewing settings");
        startActivity(m_webviewIntent);
    }

    public void onPause()
    {
        Log.d(TAG, "App Paused");
        super.onPause();
    }

    public void onResume()
    {
        Log.d(TAG, "App Resumed");
        super.onResume();
    }
}
