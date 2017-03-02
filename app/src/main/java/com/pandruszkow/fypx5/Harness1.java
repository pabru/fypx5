package com.pandruszkow.fypx5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pandruszkow.fypx5.protocol.Protocol;
import com.pandruszkow.fypx5.protocol.message.ProtocolMessage;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

public class Harness1 extends AppCompatActivity implements ToastableActivity, View.OnClickListener{

    TextView har1_txtV;
    Protocol proto;


    public static final String TAG = "Harness1";
    public SalutDataReceiver dataReceiver;
    public SalutServiceData serviceData;
    public Salut network;
    public Button hostingBtn;
    public Button discoverBtn;

    private SalutDataCallback recv = (data) -> {
        toast(data.toString());
    } ;

    @Override
    public void onClick(View v) {

        if(!Salut.isWiFiEnabled(getApplicationContext()))
        {
            Toast.makeText(getApplicationContext(), "Please enable WiFi first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(v.getId() == R.id.hosting_button)
        {
            if(!network.isRunningAsHost)
            {
                network.startNetworkService((salutDevice) -> {
                        Toast.makeText(getApplicationContext(), "Device: " + salutDevice.instanceName + " connected.", Toast.LENGTH_SHORT).show();
                });

                hostingBtn.setText("Stop Service");
                discoverBtn.setAlpha(0.5f);
                discoverBtn.setClickable(false);
            }
            else
            {
                network.stopNetworkService(false);
                hostingBtn.setText("Start Service");
                discoverBtn.setAlpha(1f);
                discoverBtn.setClickable(true);
            }
        }
        else if(v.getId() == R.id.discover_services)
        {
            if(!network.isRunningAsHost && !network.isDiscovering)
            {
                network.discoverNetworkServices(() -> {
                        Toast.makeText(getApplicationContext(), "Device: " + network.foundDevices.get(0).instanceName + " found.", Toast.LENGTH_SHORT).show();

                }, true);
                discoverBtn.setText("Stop Discovery");
                hostingBtn.setAlpha(0.5f);
                hostingBtn.setClickable(false);
            }
            else
            {
                network.stopServiceDiscovery(true);
                discoverBtn.setText("Discover Services");
                hostingBtn.setAlpha(1f);
                hostingBtn.setClickable(false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harness1);

        hostingBtn = (Button) findViewById(R.id.hosting_button);
        discoverBtn = (Button) findViewById(R.id.discover_services);

        hostingBtn.setOnClickListener(this);
        discoverBtn.setOnClickListener(this);


        dataReceiver = new SalutDataReceiver(this, recv);
        /*Populate the details for our awesome service. */
        serviceData = new SalutServiceData("fypx6", 60606,
                "HOST");

        /*Create an instance of the Salut class, with all of the necessary data from before.
        * We'll also provide a callback just in case a device doesn't support WiFi Direct, which
        * Salut will tell us about before we start trying to use methods.*/
        network = new Salut(dataReceiver, serviceData, ()->{
                // wiFiFailureDiag.show();
                // OR
                Log.e(TAG, "Sorry, but this device does not support WiFi Direct.");
            });

        har1_txtV = (TextView) findViewById(R.id.har1_txt);
    }

    public void onClick_listFound(View v){
        StringBuilder found = new StringBuilder();

        for(SalutDevice dev : network.foundDevices)
        {
            found.append(dev.toString()).append('\n');
        }

        har1_txtV.setText(found.toString());
    }

    public void onClick_beDiscoverable(View v){

        proto = new Protocol(this, Protocol.ROLE.SERVER);


    }

    public void onClick_discover(View v){

        proto = new Protocol(this, Protocol.ROLE.CLIENT);


    }



    public void toast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
