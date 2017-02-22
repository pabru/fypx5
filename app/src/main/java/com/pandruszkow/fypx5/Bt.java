package com.pandruszkow.fypx5;

import android.content.Context;
import android.util.Log;

import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by piotrek on 22/02/17.
 */
public class Bt {

    private static final String TAG = Bt.class.getCanonicalName();

    private static SmoothBluetooth mSmoothBluetooth;
    public static Bt.STATE connectionState = null;
    public static boolean isInDiscoveryMode = false;
    public static List<Device> devicesFound = null;


    public static List<Device> discover(){
        mSmoothBluetooth.doDiscovery();
        return devicesFound;
    }

    public static void init(Context context){


        mSmoothBluetooth = new SmoothBluetooth(context,
                SmoothBluetooth.ConnectionTo.ANDROID_DEVICE,
                SmoothBluetooth.Connection.INSECURE,
                new SmblListener());

    }

    public enum STATE {
        CONNECTING,
        CONNECTED,
        DISCONNECTED
    }

    private static class SmblListener implements SmoothBluetooth.Listener {

        @Override
        public void onBluetoothNotSupported() {
            throw new RuntimeException("BT not supported");
        }

        @Override
        public void onBluetoothNotEnabled() {
            throw new RuntimeException("BT not enabled on device");
            //TODO add code to redirect user to Bluetooth enable here
        }

        @Override
        public void onConnecting(Device device) {
            Bt.connectionState = STATE.CONNECTING;
        }

        @Override
        public void onConnected(Device device) {
            Bt.connectionState = STATE.CONNECTED;
        }

        @Override
        public void onDisconnected() {
            Bt.connectionState = STATE.DISCONNECTED;
        }

        @Override
        public void onConnectionFailed(Device device) {

        }

        @Override
        public void onDiscoveryStarted() {
            Bt.isInDiscoveryMode = true;
        }

        @Override
        public void onDiscoveryFinished() {
            Bt.isInDiscoveryMode = false;
        }

        @Override
        public void onNoDevicesFound() {

        }

        @Override
        public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {
            Bt.devicesFound = deviceList;
        }

        @Override
        public void onDataReceived(int data) {
            Log.d(TAG, "Received data as int: " + data + " and as char: "+ ((char) data));
        }
    }
}
