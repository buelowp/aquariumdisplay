package com.home.pete.aquarium;

import com.google.android.things.pio.*;
import android.util.Log;
import java.io.IOException;
import android.content.*;
import android.support.v4.content.LocalBroadcastManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pete on 5/31/17.
 */

public class MicroCom {
    private final String TAG = this.getClass().getName();
    private static final String DEV_NAME = "UART0";

    private PeripheralManagerService m_manager = new PeripheralManagerService();
    private UartDevice m_device;
    private Context m_context;
    public boolean m_helloReceived;

    public MicroCom(Context context)
    {
        m_context = context;
        try {
            List<String> deviceList = m_manager.getUartDeviceList();
            if (deviceList.isEmpty()) {
                Log.i(TAG, "No device available.");
            } else {
                Log.i(TAG, "List of available devices: " + deviceList);
            }
            m_device = m_manager.openUartDevice(DEV_NAME);
            m_device.registerUartDeviceCallback(mUartCallback);
            configureDevice();
        }
        catch (IOException e) {
            Log.e(TAG, "Error starting UART device " + DEV_NAME + ": " + e.getMessage());
        }

        m_helloReceived = false;
    }

    public void close() {
        byte buf[] = { (byte)0xF0, (byte)0xFF, (byte)0x00, (byte)0xF1 };

        try {
            writeData(buf);
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void sendHello() {
        MessagePayload msg = new MessagePayload();
        msg.sendHello();
        msg.makeFinal();

        try {
            writeData(msg.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void sendLedColor(byte r, byte g, byte b) {
        MessagePayload msg = new MessagePayload();
        msg.setColor(r, g, b);
        msg.makeFinal();

        try {
            writeData(msg.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void sendLedBrightness(byte b) {
        MessagePayload msg = new MessagePayload();
        msg.setBrightness(b);
        msg.makeFinal();

        try {
            writeData(msg.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void requestWaterLevel() {
        MessagePayload msg = new MessagePayload();
        msg.getWaterLevel();
        msg.makeFinal();

        try {
            writeData(msg.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void requestTemps() {
        MessagePayload msg = new MessagePayload();
        msg.getTemps();
        msg.makeFinal();

        try {
            writeData(msg.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void toggleUV() {
        MessagePayload msg = new MessagePayload();
        msg.toggleUVState();
        msg.makeFinal();

        try {
            writeData(msg.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void configureDevice() throws IOException {
        m_device.setBaudrate(115200);
        m_device.setDataSize(8);
        m_device.setParity(UartDevice.PARITY_NONE);
        m_device.setStopBits(1);
    }

    private void writeData(byte[] buf) throws IOException {
        m_device.write(buf, buf.length);
    }

    private void parseMessage(byte[] buf, int length) {
        if (((buf[0] & 0xFF) == 0xF0) && ((buf[length - 1] & 0xFF) == 0xF1)) {

            switch ((buf[2] & 0xFF)) {
                case 0xAA: {
                    Log.d(TAG, "Init response received, we can talk to the Teensy");
                    m_helloReceived = true;
                    Intent msg = new Intent("teensy-event-hello");
                    msg.putExtra("ACTION", 1);
                    Log.d(TAG, "Got a handshake reponse");
                    LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg);
                    break;
                }
                case 0x03: {
                    byte[] f = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        f[i] = buf[i + 3];
                    }
                    float response = ByteBuffer.wrap(f).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    Intent msg = new Intent("teensy-event-waterlevel");
                    msg.putExtra("ACTION", response);
                    Log.d(TAG, "Received a water level from the Teensy of value " + response);
                    LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg);
                    break;
                }
                case 0x04: {
                    if ((buf[3] & 0xff) == 0x08) {
                        byte[] r = new byte[4];
                        byte[] l = new byte[4];
                        for (int i = 0; i < 4; i++) {
                            r[i] = buf[i + 3];
                            l[i] = buf[i + 7];
                        }
                        float left = ByteBuffer.wrap(l).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        float right = ByteBuffer.wrap(r).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        Intent msgl = new Intent("teensy-event-temp-left");
                        Intent msgr = new Intent("teensy-event-temp-right");
                        msgl.putExtra("ACTION", left);
                        msgr.putExtra("ACTION", right);
                        Log.d(TAG, "Received temperatures from the Teensy");
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msgl);
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msgr);
                    }
                    break;
                }
            }
        }
    }

    private void readUartBuffer(UartDevice uart) throws IOException {
        // Maximum amount of data to read at one time
        final int maxCount = 32;
        byte[] buf = new byte[maxCount];

        int count;
        count = uart.read(buf, buf.length);

        if ((buf[0] & 0xFF) == 0xF0) {
            parseMessage(buf, count);
        }
        else {
            Log.e(TAG, "Unknown message starting with " + (buf[0] & 0xFF));
        }
    }

    private UartDeviceCallback mUartCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            // Read available data from the UART device
            try {
                readUartBuffer(uart);
            }
            catch (IOException e) {
                Log.w(TAG, "Unable to access UART device", e);
            }

            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w(TAG, uart + ": Error event " + error);
        }
    };
}
