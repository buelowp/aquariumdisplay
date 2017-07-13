package com.home.pete.aquarium;

import android.os.Bundle;
import android.app.Activity;
import android.view.GestureDetector;
import android.content.Context;
import android.view.MotionEvent;
import android.util.Log;
//import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

public class WebviewActivity extends Activity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context m_context;
    private GestureDetector m_gd;
    private List<String> m_urlList;
    private int m_index;
//    private WebView m_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        this.m_context = m_context;
        m_gd = new GestureDetector(m_context, this);
        m_gd.setOnDoubleTapListener(this);
        m_urlList = new ArrayList<String>();

        m_urlList.add("https://en.wikipedia.org/wiki/Neon_tetra");
        m_urlList.add("https://en.wikipedia.org/wiki/Dwarf_gourami");
        m_urlList.add("https://en.wikipedia.org/wiki/Hypostomus_plecostomus");
        m_urlList.add("https://en.wikipedia.org/wiki/Pterophyllum");
        m_urlList.add("http://www.tropical-fish-keeping.com/candy-cane-tetra-hyphessobrycon-bentosi.html#sthash.7X2PMI9Q.dpbs");
        m_urlList.add("http://animal-world.com/encyclo/fresh/anabantoids/PlatinumGourami.php");
        m_urlList.add("https://en.wikipedia.org/wiki/Siamese_algae_eater");
        m_urlList.add("https://aquaticarts.com/pages/electric-blue-crayfish-care-guide");
        m_urlList.add("https://en.wikipedia.org/wiki/Serpae_tetra");

        m_index = 0;
/*        try {
            m_view = new WebView(this);
        }
        catch ( e) {

        } */
    }

    private void nextEntry()
    {/*
        if (m_index < m_urlList.size())
            m_view.loadUrl(m_urlList.get(m_index++));
        else
            m_index = 0;*/
    }

    private void previousEntry()
    {/*
        if (m_index > 0)
            m_view.loadUrl(m_urlList.get(m_index--));
        else if (m_index == 0) {
            m_view.loadUrl(m_urlList.get(m_index));
            m_index = m_urlList.size() - 1;
        }*/
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(TAG, "onShowPress: " + event.toString());
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        if (event1.getRawX() > event2.getRawX()) {
            previousEntry();
            Log.d(TAG, "onFling: swipe right");
        }
        else {
            nextEntry();
            Log.d(TAG, "onFling: swipe left");
        }

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(TAG, "onSingleTapConfirmed: " + event.toString());
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(TAG, "onSingleTapUp: " + event.toString());
        return false;

    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(TAG, "onDoubleTap: " + event.toString());
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll: " + e1.toString()+e2.toString());
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        this.finish();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(TAG,"onDown: " + event.toString());
        return false;
    }
}
