package com.home.pete.aquarium;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by pete on 7/8/17.
 */

public class MessagePayload {
    private final String TAG = this.getClass().getName();
    private static int m_messageSize = 1;
    private static int m_messageCount = 2;
    private byte m_internal[] = new byte[256];
    private int m_currIndex;
    private int m_currentMessageSize;

    /**
     * Create message structure
     * First byte is always 0xF0
     * Second byte is always total size minus start and end bytes
     * Third byte is number of messages contained inside
     * From byte 4 and on is the payload
     */
    public MessagePayload()
    {
        m_internal[0] = (byte)0xF0;
        m_internal[m_messageSize] = (byte)0x00;
        m_internal[m_messageCount] = 0;

        m_currIndex = 3;
        m_currentMessageSize = 3;
    }

    public void setBrightness(byte b)
    {
        m_internal[m_currIndex++] = (byte)0x02;
        m_internal[m_currIndex++] = (byte)0x01;
        m_internal[m_currIndex++] = b;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x03;
        m_currentMessageSize += 3;
    }

    public void setColor(byte r, byte g, byte b)
    {
        m_internal[m_currIndex++] = (byte)0x01;
        m_internal[m_currIndex++] = (byte)0x03;
        m_internal[m_currIndex++] = r;
        m_internal[m_currIndex++] = g;
        m_internal[m_currIndex++] = b;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x05;
        m_currentMessageSize += 5;
    }

    public void toggleUVState()
    {
        m_internal[m_currIndex++] = (byte)0x05;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void togglePumpState()
    {
        m_internal[m_currIndex++] = (byte)0x0D;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void toggleHeaterState()
    {
        m_internal[m_currIndex++] = (byte)0x0E;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void getPumpState()
    {
        m_internal[m_currIndex++] = (byte)0x08;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void getHeaterState()
    {
        m_internal[m_currIndex++] = (byte)0x09;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void toggleSunLights()
    {
        m_internal[m_currIndex++] = (byte)0x0A;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void toggleAllLights()
    {
        m_internal[m_currIndex++] = (byte)0x0B;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void powerOffSystem()
    {
        m_internal[m_currIndex++] = (byte)0x10;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void sendHello()
    {
        m_internal[m_currIndex++] = (byte)0xAA;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void getWaterLevel()
    {
        m_internal[m_currIndex++] = (byte)0x03;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void getTemps()
    {
        m_internal[m_currIndex++] = (byte)0x04;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void makeFinal()
    {
        m_internal[m_currIndex] = (byte)0xF1;
        m_internal[m_messageSize] += 1;
        m_currentMessageSize++;
        Log.d(TAG, "Finalizing a message of size " + m_currentMessageSize);
        Log.d(TAG, "Finalized contents: " + Arrays.toString(getMessage()));
    }

    public void getUVState()
    {
        m_internal[m_currIndex++] = (byte)0x07;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public void getBrightness()
    {
        m_internal[m_currIndex++] = (byte)0x0C;
        m_internal[m_currIndex++] = (byte)0x00;
        m_internal[m_messageCount] += (byte)0x01;
        m_internal[m_messageSize] += (byte)0x02;
        m_currentMessageSize += 2;
    }

    public byte[] getMessage()
    {
        byte msg[] = new byte[m_currentMessageSize];
        for (int i = 0; i < (m_currentMessageSize); i++) {
            msg[i] = m_internal[i];
        }

        return msg;
    }
}
