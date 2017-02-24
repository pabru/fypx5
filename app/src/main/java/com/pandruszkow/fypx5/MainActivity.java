package com.pandruszkow.fypx5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    //wp2p
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    //end wp2p

    TextView statusView = null;
    TextView textReceivedView = null;

    EditText messageText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusView = (TextView)findViewById(R.id.status_lbl);
        textReceivedView = (TextView)findViewById(R.id.textReceived_lbl);
        messageText = (EditText)findViewById(R.id.msg_text);

        //wifi p2p setup
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


    }
    public void discover(View v){
        //TODO Wp2p code here

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                toast("ended discover");
            }

            @Override
            public void onFailure(int reasonCode) {
                toast("failed at discoverPeers");
            }
        });

    }

    public void onPeersChange(WifiP2pDeviceList peers){
        statusView.setText("Found new peers");
        toast("New peers found");
        String peerNamesText = "";
        for(WifiP2pDevice d : peers.getDeviceList()){
            peerNamesText += d.deviceName + ", ";
        }
        textReceivedView.setText(peerNamesText);
    }

    public void advertise(View v) {

        //todo wp2p code here

    }

    public void addToStoreAndSync(View v){

    }

    public void becomeClient(View v){


    }

    public void becomeServer(View v){

    }

    public void toast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }




    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

}
