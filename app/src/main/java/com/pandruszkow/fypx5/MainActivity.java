package com.pandruszkow.fypx5;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;



import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final String[] permissionsToRequest =
            {"android.permission.ACCESS_FINE_LOCATION"};
    private static final String ble_uuid = "3e75bbea-be47-43c4-9188-ccf44016f503";
    private static final ParcelUuid pUuid = new ParcelUuid( UUID.fromString( ble_uuid ) );

    private static final BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();

    TextView statusView = null;
    TextView textReceivedView = null;

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if( result == null || result.getDevice() == null) {
                return;
            }
            ScanRecord sr = result.getScanRecord();
            String devDescription =
                    "Raw bytes: " + sr.getServiceData(pUuid) + "\n"
                    + "String: " + new String(sr.getServiceData(pUuid), StandardCharsets.UTF_8) + "\n"
                    + "Device name: " + sr.getDeviceName();


            textReceivedView.setText(devDescription);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e( "BLE", "Discovery onScanFailed: " + errorCode );
            super.onScanFailed(errorCode);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusView = (TextView)findViewById(R.id.status_lbl);
        textReceivedView = (TextView)findViewById(R.id.textReceived_lbl);

        requestPermissions(permissionsToRequest, 999);


    }
    public void discover(View v){
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        ScanSettings sSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        mBluetoothLeScanner.startScan(mScanCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }, 10000);
    }


    public void advertise(View v) {

        AdvertiseSettings bcastSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setTimeout(10000)
                .build();

        String sampleBleData = "I am a sample text";

        AdvertiseData bcastData = new AdvertiseData.Builder()
                .addServiceUuid(pUuid)
                .addServiceData(pUuid, giveUtf8EncodedSlice(sampleBleData, 0, 4))
                .build();

        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }
        };

        advertiser.startAdvertising( bcastSettings, bcastData, advertisingCallback );

        statusView.setText("Advertise mode on");

    }

    private byte[] giveUtf8EncodedSlice(String src, int rangeStartIncl, int rangeEndExcl){
        return Arrays.copyOfRange(src.getBytes(StandardCharsets.UTF_8), rangeStartIncl, rangeEndExcl);
    }
}
