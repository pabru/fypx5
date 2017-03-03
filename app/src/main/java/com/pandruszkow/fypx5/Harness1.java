package com.pandruszkow.fypx5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.pandruszkow.fypx5.protocol.Config;
import com.pandruszkow.fypx5.protocol.ServerProtocol;
import com.pandruszkow.fypx5.protocol.Protocol;
import com.pandruszkow.fypx5.protocol.message.ProtocolMessage;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

import java.io.IOException;

public class Harness1 extends Activity implements ToastableActivity, SalutDataCallback{

    TextView har1_txtV;
    Protocol proto;


    public static final String TAG = "Harness1";
    public SalutServiceData serviceData;
    public Salut network;
    public Button hostingBtn;
    public Button discoverBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harness1);

        hostingBtn = (Button) findViewById(R.id.hosting_button);
        discoverBtn = (Button) findViewById(R.id.discover_services);
        Button thirdBtn = (Button) findViewById(R.id.third_button_h1);
        thirdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick_thirdB(view);
            }
        });

        proto = new ServerProtocol();

        network = new Salut(
                new SalutDataReceiver(this, this),
                Config.salutServiceData,
                //callback in case wifi direct fails
                new SalutCallback() {
                    @Override
                    public void call() {
                        toast("Wifi Direct not supported on this device");
                    }
                }
        );


        har1_txtV = (TextView) findViewById(R.id.har1_txt);
    }

    @Override
    public void onDataReceived(Object o){
        try {
            ProtocolMessage pM = LoganSquare.parse(o.toString(), ProtocolMessage.class);
        } catch (IOException ioe){
            toast("Received corrupt or malformed message: "+o.toString());
        }
        toast("Received data! : "+(String)o);
    }

    public void onClick_serverButton(View v) {

        if (!network.isRunningAsHost) {
            network.startNetworkService(new SalutDeviceCallback() {
                @Override
                public void call(SalutDevice salutDevice) {
                    toast("Device: " + salutDevice.instanceName + " connected to server.");
                }
            });
            proto = new ServerProtocol();

            hostingBtn.setText("Stop Service");
            discoverBtn.setAlpha(0.5f);
            discoverBtn.setClickable(false);
        } else {
            network.stopNetworkService(false);
            hostingBtn.setText("Start Service");
            discoverBtn.setAlpha(1f);
            discoverBtn.setClickable(true);
        }

    }
    public void onClick_discoverButton(View v){
        if(!network.isRunningAsHost && !network.isDiscovering)
        {
            network.discoverNetworkServices(new SalutCallback() {
                @Override
                public void call() {
                    for(SalutDevice dev : network.foundDevices) {
                        toast("Server: " + dev.instanceName + " found.");
                    }
                }
            }, true);
            discoverBtn.setText("Stop Discovery");
            hostingBtn.setAlpha(0.5f);
            hostingBtn.setClickable(false);
        }
        else
        {
            network.stopServiceDiscovery(false);
            discoverBtn.setText("Discover Services");
            hostingBtn.setAlpha(1f);
            hostingBtn.setClickable(false);
        }

    }



    public void onClick_thirdB(View v){
        //Class goTo = NoticeBoardActivity.class;

        Class goTo = Harness2.class;

        Intent postNewI = new Intent(this, goTo);
        //startActivity(postNewI);

        final SalutDevice srv = network.foundDevices.get(0);

        if(network.registeredHost==null){
            network.registerWithHost(srv,
                    new SalutCallback() {
                        @Override
                        public void call() {
                            toast("Registered with " + srv.toString());
                        }
                    },
                    new SalutCallback() {
                        @Override
                        public void call() {
                            toast("Failed to register with " + srv.toString());
                        }
                    }
            );
        } else {
            network.sendToHost(
                    ProtocolMessage.hello(),
                    new SalutCallback() {
                        @Override
                        public void call() {
                            Log.w(TAG,"failed to send data to host!");
                        }
                    });
        }

    }




    public void toast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
