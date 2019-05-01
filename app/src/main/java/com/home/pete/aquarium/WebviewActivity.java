package com.home.pete.aquarium;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;
import java.util.List;

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
public class WebviewActivity extends Activity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener
{
    private static final String TAG = WebviewActivity.class.getSimpleName();

    private WebView m_webview;
    private GestureDetector mDetector;
    private List<String> m_urlList;
    private int m_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        m_webview = findViewById(R.id.webview);
        mDetector = new GestureDetector(this,this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);
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
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        Log.d(TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(TAG, "onDoubleTap: " + event.toString());
        m_index = 0;
        finish();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        m_index = 0;
        finish();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(TAG, "onLongPress: " + event.toString());
    }

}
