package com.home.pete.aquarium;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class MyWebView extends WebView {
    private static final String TAG = MyWebView.class.getSimpleName();
    Context m_context;
    GestureDetector gd;
    private List<String> m_urlList;
    private int m_index;
    private WebView m_webView;
    Activity m_webViewActivity;

    public MyWebView(Context context, Activity activity) {
        super(context);

        this.m_context = context;
        this.m_webViewActivity = activity;

        m_urlList = new ArrayList<String>();
        Log.d(TAG, "Allocated the url list and the gesture detector");

        m_webView = m_webViewActivity.findViewById(R.id.webview);
        m_webView.setWebViewClient(new WebViewClient());
        gd = new GestureDetector(m_webView.getContext(), sogl);

        m_urlList.add("https://en.wikipedia.org/wiki/Neon_tetra");
        m_urlList.add("https://en.wikipedia.org/wiki/Dwarf_gourami");
        m_urlList.add("https://en.wikipedia.org/wiki/Hypostomus_plecostomus");
        m_urlList.add("https://en.wikipedia.org/wiki/Pterophyllum");
        m_urlList.add("http://www.tropical-fish-keeping.com/candy-cane-tetra-hyphessobrycon-bentosi.html#sthash.7X2PMI9Q.dpbs");
        m_urlList.add("http://animal-world.com/encyclo/fresh/anabantoids/PlatinumGourami.php");
        m_urlList.add("https://en.wikipedia.org/wiki/Siamese_algae_eater");
        m_urlList.add("https://aquaticarts.com/pages/electric-blue-crayfish-care-guide");
        m_urlList.add("https://en.wikipedia.org/wiki/Serpae_tetra");
        Log.d(TAG, "Created all applicable URL's");

        m_index = 0;
    }

    public void loadUrl(int index)
    {
        Log.d(TAG, "Loading URL at index " + index);
        m_webView.loadUrl(m_urlList.get(index));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gd.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
        public boolean onDown(MotionEvent event) {
            return true;
        }

        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if (event1.getRawX() > event2.getRawX()) {
                show_toast("swipe left");
                previousEntry();
            } else {
                show_toast("swipe right");
                nextEntry();
            }
            return true;
        }
    };

    void show_toast(final String text) {
        Toast t = Toast.makeText(m_context, text, Toast.LENGTH_SHORT);
        t.show();
    }

    private void nextEntry()
    {
        if (m_index < m_urlList.size() - 1) {
            m_index++;
        }
        else {
            m_index = 0;
        }
        Log.d(TAG, "Getting next entry, index: " + m_index);
    }

    private void previousEntry()
    {
        if (m_index > 0) {
            m_index--;
        }
        else
            m_index = m_urlList.size() - 1;

        Log.d(TAG, "Getting previous entry, index: " + m_index);
    }
}
