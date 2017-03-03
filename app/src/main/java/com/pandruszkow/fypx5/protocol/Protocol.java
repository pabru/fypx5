package com.pandruszkow.fypx5.protocol;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.pandruszkow.fypx5.ToastableActivity;
import com.pandruszkow.fypx5.protocol.message.*;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piotrek on 23/02/17.
 */
public class Protocol implements SalutDataCallback {

    private final static String TAG = Protocol.class.getCanonicalName();

    //stores messages we know about and have on the device
    private static Map<String, ChatMessage> messageStore = new HashMap<>();
    //where in the protocol progress we are
    private STATE protoState = STATE.HELLO;
    //are we a server or a client
    private ROLE peerRole = ROLE.SERVER;
    //the other device we're speaking to
    private SalutDevice otherDevice = null;
    //object to access the network stack
    private Salut network = null;

    public static void storeMessage(ChatMessage msg){
        messageStore.put(msg.messageHash, msg);
    }

    public Protocol(final ToastableActivity parent, Salut network){
        //note - it is assumed we start off as a server and switch to a client to "go outwards" for message sync

        this.network = network;
        //become server
        this.network.startNetworkService(new SalutDeviceCallback() {
            @Override
            public void call(SalutDevice salutDevice) {
                parent.toast(salutDevice.readableName + " has connected!");
            }
        });
    }

    public void runMessageStoreSync(WifiP2pDevice peer){

        // it is assumed that we will switch to client mode for the duration
        this.network.stopNetworkService(false);

        peerRole = ROLE.CLIENT;
        this.network.discoverNetworkServices(new SalutDeviceCallback() {
            @Override
            public void call(SalutDevice salutDevice) {
                Log.d(TAG, "A device has connected with the name " + salutDevice.deviceName);
            }
        }, true);

        say(ProtocolMessage.hello());

        List<String> theirHashes = sync_hashes(new ArrayList<>(messageStore.keySet()));
        Map<String, ChatMessage> messagesToSend = findOurMessagesNotInTheirMessageStore(theirHashes);

        Map<String, ChatMessage> theirMessagesWeAreMissing = sync_messages(messagesToSend);

    }


    private Map<String,ChatMessage> sync_messages(Map<String,ChatMessage> ourMessagesTheyAreMissing){

        Map<String,ChatMessage> theirMessagesWeAreMissing;

        if(isClient()){
            say(ProtocolMessage.sync_messages(ourMessagesTheyAreMissing));
            theirMessagesWeAreMissing = listenTo_sync_messages();
        } else {
            theirMessagesWeAreMissing = listenTo_sync_messages();
            say(ProtocolMessage.sync_messages(ourMessagesTheyAreMissing));
        }

        return theirMessagesWeAreMissing;
    }

    private List<String> sync_hashes(List<String> ourHashes){
        ProtocolMessage pmOurHashes = ProtocolMessage.sync_hashes(ourHashes);
        ProtocolMessage pmTheirHashes;

        if(isClient()) {
            say(pmOurHashes);
            pmTheirHashes = listen();
        } else {
            pmTheirHashes = listen();
            say(pmOurHashes);
        }

        if(pmTheirHashes.pMsgType.equals(ProtocolMessage.TYPE.sync_hashes.toString())){
            return pmTheirHashes.requestedMessageHashes;
        } else {
            //error occurred
            return null;
        }

    }

    private Map<String, ChatMessage> findOurMessagesNotInTheirMessageStore(List<String> theirHashes) {
        //filter to find which of our messages client is missing, i.e. in our store but not in their store.
        List<String> ourHashes = new ArrayList<>(messageStore.keySet());
        List<String> messagesToSend_hashes = new ArrayList<>();

        messagesToSend_hashes.addAll(ourHashes);
        messagesToSend_hashes.removeAll(theirHashes);

        return filterMap(messagesToSend_hashes, messageStore);
    }


    private Map<String, ChatMessage> listenTo_sync_messages(){
        Map<String, ChatMessage> out = new HashMap<>();

        ProtocolMessage pMsg = listen();
        if(pMsg.pMsgType.equals(ProtocolMessage.TYPE.sync_messages)){
            for (ChatMessage msg : pMsg.chatMessages){
                out.put(msg.messageHash, msg);
            }
            return out;
        } else {
            return null;
        }
    }

    private static Map<String, ChatMessage> filterMap(List<String> retainFilter, Map<String, ChatMessage> map){
        Map<String, ChatMessage> out = new HashMap<>();

        for(String k : retainFilter){
            out.put(k, map.get(k));
        }

        return out;

    }

    private void say(final ProtocolMessage pMsg){
        if(isClient()){
            network.sendToHost(pMsg, new SalutCallback() {
                @Override
                public void call() {
                    Log.d(TAG, "Failed to send message to host device: " + pMsg.toString());
                }
            });
        } else {
            network.sendToDevice(otherDevice, pMsg, new SalutCallback() {
                @Override
                public void call() {
                    Log.d(TAG, "Failed to send message to  device: " + pMsg.toString());
                }
            });
        }
    }
    private ProtocolMessage listen(){
        //TODO!
        return null;
    }
    private boolean listenTo(ProtocolMessage expectedReply){
        return listen().equals(expectedReply);
    }
    private boolean isClient(){
        return peerRole.equals(ROLE.CLIENT);
    }
    private boolean isServer() { return peerRole.equals(ROLE.SERVER); }
    @Override
    public void onDataReceived(Object o) {
        try {
            Log.d(TAG, "received following object in onDataReceived: " + o.toString());
            ProtocolMessage pM = LoganSquare.parse((String) o, ProtocolMessage.class);
            ProtocolMessage.TYPE type = ProtocolMessage.TYPE.valueOf(pM.pMsgType);

            switch (protoState) {
                case HELLO:
                    if(isServer()){
                        say(ProtocolMessage.hello());
                    }  //else we don't care
                    break;
                case SYNC_HASH:

                    break;
                case SYNC_MSGS:

                    break;
                case BYE:
                    if(isServer()){
                        say(ProtocolMessage.bye());
                    } //else we don't care
                    break;
            }


        } catch (IllegalArgumentException iae){
            Log.w(TAG, "Unknown Protocol Message type in onDataReceived!");
        } catch (IOException ioe){
            Log.w(TAG, "Erroneous, malformed or missing data received!");
        }
    }

    public enum ROLE {
        CLIENT,
        SERVER
    }
    public enum STATE {
        HELLO,
        SYNC_HASH,
        SYNC_MSGS,
        BYE
    }
}
