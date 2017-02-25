package com.pandruszkow.fypx5.protocol;

import android.app.Activity;
import android.util.Log;

import com.pandruszkow.fypx5.MainActivity;
import com.pandruszkow.fypx5.protocol.message.ChatMessage;
import com.pandruszkow.fypx5.protocol.message.ProtocolMessage;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutServiceData;

import java.util.ArrayList;
import java.util.Arrays;
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


    private void main_clientSide(){
        ProtocolMessage clientRequest, serverReply;

        //handshake stage
        hello_clientSide();

        //message hash exchange stage
        say_sync_hashes();
        String[] server_messageHashes = listenTo_sync_hashes();

        //filter to find which of our messages server is missing
        List<String> toSend_sync_hashes = new ArrayList<>();
        // clientStore NOT IN serverStore
        toSend_sync_hashes.addAll(messageStore.keySet());
        toSend_sync_hashes.removeAll(Arrays.asList(server_messageHashes));

        Map<String, ChatMessage> messagesToSend = filterMap(toSend_sync_hashes, messageStore);

        //exchange missing messages on both sides
        say_sync_messages(messagesToSend);
        Map<String, ChatMessage> missingMessages = listenTo_sync_messages();
        say(ProtocolMessage.bye());
        listenTo(ProtocolMessage.bye());

    }
    private void main_serverSide(){
        //handshake stage
        hello_serverSide();

        //message hash exchange stage
        String[] client_messageHashes = listenTo_sync_hashes();
        say_sync_hashes();

        //filter to find which of our messages client is missing
        List<String> messagesToSend_hashes = new ArrayList<>();
        // clientStore NOT IN serverStore
        messagesToSend_hashes.addAll(messageStore.keySet());
        messagesToSend_hashes.removeAll(Arrays.asList(client_messageHashes));

        Map<String, ChatMessage> messagesToSend = filterMap(messagesToSend_hashes, messageStore);

        //exchange missing messages on both sides
        Map<String, ChatMessage> missingMessages = listenTo_sync_messages();
        say_sync_messages(messagesToSend);
        listenTo(ProtocolMessage.bye());
        say(ProtocolMessage.bye());

    }

    private void say_sync_messages(Map<String, ChatMessage> messages){
        say(ProtocolMessage.sync_messages(messages));
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




    private void say_MessageStoreHashes(){

    }



    private boolean hello_clientSide(){
        say(ProtocolMessage.hello());
        return listenTo(ProtocolMessage.hello());
    }
    private boolean hello_serverSide(){

        boolean hello_ok = listenTo(ProtocolMessage.hello());

        if(hello_ok) {
            say(ProtocolMessage.hello());
        }

        return hello_ok;
    }

    private void say_sync_hashes() {
        ProtocolMessage hashDumpMsg = ProtocolMessage.sync_hashes(new ArrayList<>(messageStore.keySet()));
        say(hashDumpMsg);
    }
    private String[] listenTo_sync_hashes(){
        //TODO!


        return null;
    }

    private String getMessageHashDump() {
        StringBuilder messageHashDumpSb = new StringBuilder();

        for(String messageHash : messageStore.keySet()){
            messageHashDumpSb.append(messageHash).append('\n');
        }
        return messageHashDumpSb.toString();
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
