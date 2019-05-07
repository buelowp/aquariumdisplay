package com.home.pete.aquarium;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;
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
public class WebViewActivity extends Activity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener
{
    private static final String TAG = WebViewActivity.class.getSimpleName();

    private WebView m_webview;
    private GestureDetector m_detector;
    private List<String> m_urlList;
    private int m_index;
    Handler m_exitHandler = new Handler();
    boolean m_gestureAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        m_gestureAvailable = false;

        m_webview = findViewById(R.id.webViewFishView);
        try {
            m_detector = new GestureDetector(m_webview.getContext(), this);
            m_detector.setOnDoubleTapListener(this);
            m_gestureAvailable = true;
        }
        catch (NullPointerException e) {
            Log.e(TAG, "Unable to instantiate Gesture Detection");
        }

        m_urlList.add("https://en.wikipedia.org/wiki/Neon_tetra");
        m_urlList.add("https://en.wikipedia.org/wiki/Hemigrammus_erythrozonus");
        m_urlList.add("https://en.wikipedia.org/wiki/Hypostomus_plecostomus");
        m_urlList.add("https://en.wikipedia.org/wiki/Pterophyllum");
        m_urlList.add("https://en.wikipedia.org/wiki/Ram_cichlid");
        m_urlList.add("https://en.wikipedia.org/wiki/Siamese_algae_eater");
        m_urlList.add("https://en.wikipedia.org/wiki/GloFish");
        m_urlList.add("https://en.wikipedia.org/wiki/Serpae_tetra");
        Log.d(TAG, "Created all applicable URL's");

        m_index = 0;
        m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
    }

    @Override
    protected void onStart() {
        if (m_index >= 0 && m_index < m_urlList.size())
            m_webview.loadUrl(m_urlList.get(m_index));
        else {
            m_index = 0;
            m_webview.loadUrl(m_urlList.get(m_index));
        }

        super.onStart();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (m_gestureAvailable) {
            if (this.m_detector.onTouchEvent(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event)
    {
        Log.d(TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY)
    {
        Log.d(TAG, "onFling: " + e1.toString() + e2.toString());
        m_exitHandler.removeCallbacks(exitViewOnTimeout);
        m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY)
    {
        Log.d(TAG, "onScroll: " + e1.toString() + e2.toString());
        m_exitHandler.removeCallbacks(exitViewOnTimeout);
        m_exitHandler.postDelayed(exitViewOnTimeout, VIEW_TIMEOUT);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event)
    {
        Log.d(TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event)
    {
        Log.d(TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event)
    {
        Log.d(TAG, "onDoubleTap: " + event.toString());
        m_index = 0;
        finish();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event)
    {
        Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        m_index = 0;
        m_exitHandler.removeCallbacks(exitViewOnTimeout);
        finish();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event)
    {
        Log.d(TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event)
    {
        Log.d(TAG, "onLongPress: " + event.toString());
    }

    private Runnable exitViewOnTimeout = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Closing view due to timeout handler");
            finish();
        }
    };
}