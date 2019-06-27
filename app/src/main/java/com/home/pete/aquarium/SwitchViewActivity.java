package com.home.pete.aquarium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

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
public class SwitchViewActivity extends AppCompatActivity
{
    private static final String TAG = SwitchViewActivity.class.getSimpleName();
    Handler m_exitHandler = new Handler();
    AquariumReceiverService serviceConnector;
    public boolean m_isBound = false;
    Switch m_pumpState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_view);
        m_pumpState = findViewById(R.id.switchPumpState);

        Log.d(TAG, "onCreate");
        m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
        LocalBroadcastManager.getInstance(this).registerReceiver(controlsUpdate, new IntentFilter("controls"));
    }

    private Runnable exitViewOnTimeout = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Closing view due to timeout handler");
            finish();
        }
    };

    public void exitView(View view)
    {
        Log.d(TAG, "Closing view");
        m_exitHandler.removeCallbacks(exitViewOnTimeout);
        finish();
    }

    private ServiceConnection sc = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            AquariumReceiverService.MyLocalBinder binder = (AquariumReceiverService.MyLocalBinder) service;
            serviceConnector = binder.getService();
            m_isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            serviceConnector = null;
            m_isBound = false;
        }
    };

    public void pumpState(View view)
    {
        String payload;

        Log.d(TAG, "Changing pump state");
        m_exitHandler.removeCallbacks(exitViewOnTimeout);
        m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
        if (m_pumpState.isChecked()) {
            payload = "{\"pump\":{\"state\":\"on\"}}";
        }
        else {
            payload = "{\"pump\":{\"state\":\"off\"}}";
        }
        serviceConnector.publishMessage(Constants.CONTROL_TOPIC, 0, payload.getBytes());
    }

    private BroadcastReceiver controlsUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String json = intent.getStringExtra("ACTION");
            JSONObject reader;
            try {
                reader = new JSONObject(json);
            }
            catch (JSONException e) {
                Log.e(TAG, e.toString());
                return;
            }

            try {
                JSONObject pump = reader.getJSONObject("pump");
                if (pump.getString("state") == "on") {
                    m_pumpState.setChecked(true);
                } else if (pump.getString("state") == "off") {
                    m_pumpState.setChecked(false);
                } else {
                    Log.d(TAG, "Unknown pump state: " + pump.getString("state"));
                }
            }
            catch (JSONException e) {
                Log.e(TAG, e.toString());
                return;
            }

        }
    };

}
