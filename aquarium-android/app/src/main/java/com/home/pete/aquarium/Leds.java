package com.home.pete.aquarium;

import android.util.Log;
import com.google.android.things.contrib.driver.apa102.Apa102;
import android.graphics.Color;
import java.io.IOException;

public class Leds {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Apa102 mLedstrip;
    private static final int NUM_LEDS = 25;
    private static final int LED_BRIGHTNESS = 5; // 0 ... 31
    private static final Apa102.Mode LED_MODE = Apa102.Mode.BGR;
    private int[] mLedColors;

    Leds()
    {
        try {
            mLedColors = new int[NUM_LEDS];
            mLedstrip = new Apa102("SPI0.0", LED_MODE);
        }
        catch (IOException e) {
            Log.e(TAG, "Error initializing LED strip", e);
        }
    }

    public void setBrightness(int b)
    {
        mLedstrip.setBrightness(b);
    }

    public void setColor(Color c)
    {
        for (int i = 0; i < NUM_LEDS; i++) {
            mLedColors[i] = c.toArgb();
        }
        try {
            mLedstrip.write(mLedColors);
        }
        catch (IOException e) {
            Log.e(TAG, "Error writing LED colors", e);
        }
    }
}
