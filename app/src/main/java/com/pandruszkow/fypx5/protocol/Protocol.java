package com.pandruszkow.fypx5.protocol;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;

import com.pandruszkow.fypx5.MainActivity;
import com.pandruszkow.fypx5.protocol.message.ChatMessage;
import com.pandruszkow.fypx5.protocol.message.ProtocolMessage;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutServiceData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by piotrek on 23/02/17.
 */
public class Protocol implements SalutDataCallback{

    private final static String TAG = Protocol.class.getCanonicalName();

    public final static String applicationName = "fypx5";
    public final static int portNumber = 50123;
    public final static String peerId = ""+new Random().nextInt();

    private static Salut network = null;

    private static ROLE peerRole = ROLE.SERVER;

    public static void switchRole(ROLE newRole){
        peerRole = newRole;
    }

    private Map<String, ChatMessage> messageStore = new HashMap<>();

    void initialise(final Activity activity){
        network = new Salut(
                new SalutDataReceiver(activity, this),
                getSalutServiceData(),
                //callback in case wifi direct fails
                () -> ((MainActivity)activity).toast("Wifi Direct not supported on this device")
        );

    }

    SalutServiceData getSalutServiceData(){
        return new SalutServiceData(applicationName, portNumber, peerId);
    }

    public void runMessageStoreSync(WifiP2pDevice peer, boolean isClient){

        hello(isClient);

        List<String> theirHashes = sync_hashes(isClient, new ArrayList<>(messageStore.keySet()));
        Map<String, ChatMessage> messagesToSend = findOurMessagesNotInTheirMessageStore(theirHashes);

        Map<String, ChatMessage> theirMessagesWeAreMissing = sync_messages(isClient, messagesToSend);

        bye(isClient);
    }


    private Map<String,ChatMessage> sync_messages(boolean isClient, Map<String,ChatMessage> ourMessagesTheyAreMissing){

        Map<String,ChatMessage> theirMessagesWeAreMissing;

        if(isClient){
            say(ProtocolMessage.sync_messages(ourMessagesTheyAreMissing));
            theirMessagesWeAreMissing = listenTo_sync_messages();
        } else {
            theirMessagesWeAreMissing = listenTo_sync_messages();
            say(ProtocolMessage.sync_messages(ourMessagesTheyAreMissing));
        }

        return theirMessagesWeAreMissing;
    }

    private List<String> sync_hashes(boolean isClient, List<String> ourHashes){
        ProtocolMessage pmOurHashes = ProtocolMessage.sync_hashes(ourHashes);
        ProtocolMessage pmTheirHashes;

        if(isClient) {
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

    private boolean bye(boolean isClient){
        boolean byeSuccessful = false;

        if(isClient){
            say(ProtocolMessage.bye());
            byeSuccessful = listenTo(ProtocolMessage.bye());
        } else {
            byeSuccessful = listenTo(ProtocolMessage.bye());
            say(ProtocolMessage.bye());
        }

        return byeSuccessful;
    }
    private boolean hello(boolean isClient){
        boolean helloSuccessful = false;

        if(isClient){

            say(ProtocolMessage.hello());

            helloSuccessful = listenTo(ProtocolMessage.hello());

        } else {

            helloSuccessful = listenTo(ProtocolMessage.hello());

            if(helloSuccessful) say(ProtocolMessage.hello());
        }

        return helloSuccessful;
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

    private void say(ProtocolMessage pMsg){
        //TODO!
    }
    private ProtocolMessage listen(){
        //TODO!
        return null;
    }
    private boolean listenTo(ProtocolMessage expectedReply){
        return listen().equals(expectedReply);
    }

    @Override
    public void onDataReceived(Object o) {

    }

    public enum ROLE {
        CLIENT,
        SERVER
    }
}
