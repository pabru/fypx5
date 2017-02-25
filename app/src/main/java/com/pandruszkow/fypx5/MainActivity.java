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

import com.pandruszkow.fypx5.protocol.Protocol;

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

        //TODO wifi p2p setup



    }
    public void discover(View v){
        //TODO Wp2p code here


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
        Protocol.switchRole(Protocol.ROLE.CLIENT);

    }

    public void becomeServer(View v){
        Protocol.switchRole(Protocol.ROLE.SERVER);

    }

    public void toast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

}
