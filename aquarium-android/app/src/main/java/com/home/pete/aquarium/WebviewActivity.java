package com.home.pete.aquarium;

import android.os.Bundle;
import android.app.Activity;
import android.view.GestureDetector;
import android.content.Context;
import android.view.MotionEvent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
//import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

public class WebviewActivity extends Activity {

    private static final String TAG = WebviewActivity.class.getSimpleName();
    private Context m_context;
    MyWebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Log.d(TAG, "Getting started");
        myWebView = new MyWebView(WebviewActivity.this, this);
        Log.d(TAG, "Set web view client");

//        this.m_context = m_context;
//        m_gd = new GestureDetector(myWebView.getContext(), this);
//        m_gd.setOnDoubleTapListener(this);
//        webview.loadUrl("https://en.wikipedia.org/wiki/Dwarf_gourami");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        myWebView.loadUrl(0);
    }
}
