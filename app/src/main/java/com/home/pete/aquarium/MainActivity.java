package com.home.pete.aquarium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Intent m_webviewIntent;
    private Intent m_dataviewIntent;
    private boolean m_displayActive;
    Handler m_hideViewHandler;
    ConstraintLayout m_layout;
    ImageButton m_fishView;
    ImageButton m_dataView;
    TextView m_fishTextView;
    TextView m_dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_layout = findViewById(R.id.constraintLayout);
        m_fishView = findViewById(R.id.imageButtonFish);
        m_dataView = findViewById(R.id.imageButtonData);
        m_fishTextView = findViewById(R.id.textViewFishLabel);
        m_dataTextView = findViewById(R.id.textViewDataLabel);

        m_displayActive = true;

        Log.d(TAG, "onCreate");
        m_webviewIntent = new Intent(this, WebViewActivity.class);
        m_dataviewIntent = new Intent(this, DataViewActivity.class);
        startService(new Intent(this, AquariumReceiverService.class));
        m_hideViewHandler = new Handler();
        m_hideViewHandler.postDelayed(hideViewOnTimeout, VIEW_TIMEOUT);
    }

    @Override
    protected void onResume() {
        if (!m_displayActive) {
            setViewState(View.VISIBLE);
            m_hideViewHandler.postDelayed(hideViewOnTimeout, VIEW_TIMEOUT);
            m_displayActive = true;
        }
        super.onResume();
    }

    @Override
    protected void onStart ()
    {
        Log.d(TAG, "Visibility Changed");
        if (!m_displayActive) {
            setViewState(View.VISIBLE);
            m_hideViewHandler.postDelayed(hideViewOnTimeout, VIEW_TIMEOUT);
            m_displayActive = true;
        }
        else {
            m_hideViewHandler.removeCallbacks(hideViewOnTimeout);
            m_hideViewHandler.postDelayed(hideViewOnTimeout, VIEW_TIMEOUT);
        }
        super.onStart();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!m_displayActive) {
            setViewState(View.VISIBLE);
            m_hideViewHandler.postDelayed(hideViewOnTimeout, VIEW_TIMEOUT);
            m_displayActive = true;
        }
        return true;
    }

    public void viewData(View view)
    {
        Log.d(TAG, "Viewing settings");
        startActivity(m_dataviewIntent);
        m_hideViewHandler.removeCallbacks(hideViewOnTimeout);
    }

    public void viewFish(View view)
    {
        Log.d(TAG, "Viewing settings");
        startActivity(m_webviewIntent);
        m_hideViewHandler.removeCallbacks(hideViewOnTimeout);
    }

    private Runnable hideViewOnTimeout = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Turning off icons to avoid burn in");
            if (m_displayActive) {
                setViewState(View.INVISIBLE);
                m_displayActive = false;
            }
        }
    };

    private void setViewState(int state)
    {
        m_fishView.setVisibility(state);
        m_dataView.setVisibility(state);
        m_fishTextView.setVisibility(state);
        m_dataTextView.setVisibility(state);

    }

}
