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

    public void sendPreformattedMsg(byte[] msg) {
        try {
            writeData(msg);
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
        Log.d(TAG, "Sending: " + Arrays.toString(buf));
        m_device.write(buf, buf.length);
    }

    private void parseMessage(byte[] buf, int length) {
        Log.d(TAG, "Got a message of size: " + length);
        Log.d(TAG, "Message contents are: " + Arrays.toString(buf));

        if (((buf[0] & 0xFF) == 0xF0) && ((buf[length - 1] & 0xFF) == 0xF1)) {
            int msgSize = buf[1];
            int msgCount = buf[2];
            int index = 3;

            Log.d(TAG, "Got message of size " + msgSize + ", with " + msgCount + " messages inside");
            Log.d(TAG, "Checking message id " + (buf[index] & 0xFF) + " at index " + index);
            for (int i = 0; i < msgCount; i++) {
                switch ((buf[index++] & 0xFF)) {
                    case 0xAA: {
                        Intent msg = new Intent("teensy-event-hello");
                        msg.putExtra("ACTION", 1);
                        Log.d(TAG, "Got a handshake reponse");
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg);
                        index++;
                        m_helloReceived = true;
                        break;
                    }
                    case 0x03: {
                        int packetSize = buf[index++];
                        byte[] f = new byte[packetSize];
                        for (int j = 0; j < packetSize; j++) {
                            f[j] = buf[index + j];
                        }
                        index += packetSize;
                        float response = ByteBuffer.wrap(f).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        Intent msg = new Intent("teensy-event-waterlevel");
                        msg.putExtra("ACTION", response);
                        Log.d(TAG, "Received a water level from the Teensy of value " + response);
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg);
                        break;
                    }
                    case 0x04: {
                        int packetSize = buf[index++];
                        float left = 0;
                        float right = 0;
                        Log.d(TAG, "Got packetsize of " + packetSize + " and am at index " + index);
                        try {
                            byte[] l = Arrays.copyOfRange(buf, index, (index + 4));
                            index += 4;
                            byte[] r = Arrays.copyOfRange(buf, index, (index + 4));
                            index += 4;
                            left = ByteBuffer.wrap(l).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                            right = ByteBuffer.wrap(r).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        }
                        catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | NullPointerException e) {
                            Log.e(TAG, "Array copy exception: " + e.getMessage());
                        }
                        Log.d(TAG, "Got left temp " + left + " and right temp " + right);
                        Intent msg_l = new Intent("teensy-event-temp-left");
                        msg_l.putExtra("ACTION", left);
                        Intent msg_r = new Intent("teensy-event-temp-right");
                        msg_r.putExtra("ACTION", right);
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg_l);
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg_r);
                        break;
                    }
                    case 0x07: {
                        int packetSize = buf[index++];
                        int state = buf[index++];
                        Intent msg = new Intent("teensy-event-uvstate");
                        msg.putExtra("ACTION", state);
                        Log.d(TAG, "Got UV state of " + state);
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg);
                        break;
                    }
                    case 0x08: {
                        int packetSize = buf[index++];
                        int state = buf[index++];
                        Intent msg = new Intent("teensy-event-pumpstate");
                        msg.putExtra("ACTION", state);
                        Log.d(TAG, "Got a pump state of " + state);
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg);
                        break;
                    }
                    case 0x09: {
                        int packetSize = buf[index++];
                        int state = buf[index++];
                        Intent msg = new Intent("teensy-event-heaterstate");
                        msg.putExtra("ACTION", state);
                        Log.d(TAG, "Got a heater state of " + state);
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg);
                        break;
                    }
                    case 0x0C: {
                        int packetSize = buf[index++];
                        int state = buf[index++] & 0xFF;
                        Intent msg = new Intent("teensy-event-brightness");
                        msg.putExtra("ACTION", state);
                        Log.d(TAG, "Got an LED brightness of " + state);
                        LocalBroadcastManager.getInstance(m_context).sendBroadcast(msg);
                        break;
                    }
                }
            }
        }
    }

    private UartDeviceCallback mUartCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            // Read available data from the UART device
            try {
                byte[] buffer = new byte[256];
                int count = uart.read(buffer, buffer.length);
                if ((buffer[0] & 0xFF) == 0xF0) {
                    parseMessage(buffer, count);
                }
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
