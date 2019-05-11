package com.home.pete.aquarium;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

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
public class WebViewActivity extends Activity
{
    private static final String TAG = WebViewActivity.class.getSimpleName();

    private MyWebView m_webview;
    private GestureDetector m_detector;
    Handler m_exitHandler = new Handler();
    boolean m_gestureAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_webview = new MyWebView(this);
        setContentView(m_webview);

        m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private Runnable exitViewOnTimeout = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Closing view due to timeout handler");
            finish();
        }
    };

    public class MyWebView extends WebView {
        private boolean flinged;

        private static final int SWIPE_MIN_DISTANCE = 320;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        private ArrayList<String> m_urlList;
        private String m_url = "http://172.24.1.12/fish/fishview.php?fishname=";
        GestureDetector gd;
        private int m_index;

        public MyWebView(Context context) {
            super(context);
            gd = new GestureDetector(context, sogl);
            m_urlList = new ArrayList<String>();

            m_urlList.add("Gold_Veil_Angelfish");
            m_urlList.add("Butterfly_Plecostomus");
            m_urlList.add("Electric_Blue_Ram");
            m_urlList.add("Flying_Fox");
            m_urlList.add("White_Widow_Tetra");
            m_urlList.add("Glowlight_Tetra");
            m_urlList.add("Gold_Neon_Tetra");
            m_urlList.add("Neon_Tetra");
            m_urlList.add("Rummynose_Tetra");
            m_urlList.add("Serpae_Tetra");
            m_urlList.add("Glow_Tiger_Barb");
            m_urlList.add("Green_Neon_Tetra");
            Log.d(TAG, "Created all applicable URL's");

            m_index = 0;
            String url = m_url  + m_urlList.get(m_index);
            loadUrl(url);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            gd.onTouchEvent(event);
            if (flinged) {
                flinged = false;
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        }

        GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                if (Math.abs(event1.getY() - event1.getY()) > SWIPE_MAX_OFF_PATH) {
                    Log.d(TAG, "swipe max off path");
                    return false;
                }
                if(event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (m_index == m_urlList.size() - 1)
                        m_index = -1;
                    String url = m_url  + m_urlList.get(++m_index);
                    loadUrl(url);
                    Log.i("Swiped","swipe left");
                    flinged = true;
                } else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (m_index == 0) {
                        m_index = m_urlList.size();
                    }
                    String url = m_url  + m_urlList.get(--m_index);
                    loadUrl(url);
                    Log.i("Swiped","swipe right");
                    flinged = true;
                }
                return true;
            }
        };
    }
}