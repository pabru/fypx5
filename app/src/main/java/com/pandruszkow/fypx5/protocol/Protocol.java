package com.pandruszkow.fypx5.protocol;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piotrek on 23/02/17.
 */
public class Protocol {

    private final static String TAG = Protocol.class.getCanonicalName();

    private static final String
        HELLO = "HELLO",

        BEGIN_MESSAGEHASH_SYNC = "SYNC HASHES",
        END_MESSAGEHASH_SYNC = "END_HASHES",

        END_MESSAGES_SYNC = "END_MESSAGES",

        BEGIN_SINGLE_MESSAGE_SYNC = "SYNC ",
        END_SINGLE_MESSAGE_SYNC = "END",
        SUCCESSFUL_SINGLE_MESSAGE_SYNC = "OK",
        FAILED_RETX_PLEASE_SINGLE_MESSAGE_SYNC = "FAIL",

        BYE = "BYE";

    private Map<String, String> messageStore = new HashMap<>();


    private void main_clientSide(){
        //handshake stage
        hello_clientSide();

        //message hash exchange stage
        say_MessageDump();
        String[] server_messageHashes = listenTo_MessageDump();

        //filter to find which of our messages server is missing
        List<String> messagesToSend_hashes = new ArrayList<>();
        // clientStore NOT IN serverStore
        messagesToSend_hashes.addAll(messageStore.keySet());
        messagesToSend_hashes.removeAll(Arrays.asList(server_messageHashes));

        Map<String, String> messagesToSend = getFilteredMap(messageStore, messagesToSend_hashes);

        //exchange missing messages on both sides
        say_messageList_syncformat(messagesToSend);
        Map<String, String> missingMessages = listenTo_messageList_syncformat();
        say(BYE);
        listenTo(BYE);

    }
    private void main_serverSide(){
        //handshake stage
        hello_serverSide();

        //message hash exchange stage
        String[] client_messageHashes = listenTo_MessageDump();
        say_MessageDump();

        //filter to find which of our messages client is missing
        List<String> messagesToSend_hashes = new ArrayList<>();
        // clientStore NOT IN serverStore
        messagesToSend_hashes.addAll(messageStore.keySet());
        messagesToSend_hashes.removeAll(Arrays.asList(client_messageHashes));

        Map<String, String> messagesToSend = getFilteredMap(messageStore, messagesToSend_hashes);

        //exchange missing messages on both sides
        Map<String, String> missingMessages = listenTo_messageList_syncformat();
        say_messageList_syncformat(messagesToSend);
        listenTo(BYE);
        say(BYE);

    }

    private void say_messageList_syncformat(Map<String, String> messages){
        for(String k : messages.keySet()){
            byte transmitTryCounter = 0;
            do {
                say(BEGIN_SINGLE_MESSAGE_SYNC + k); // SYNC a6711af...
                say(Encoding.urlEncode(messages.get(k))); //<message text goes here, URL-encoded
                say(END_SINGLE_MESSAGE_SYNC); // END
                ++transmitTryCounter;

                if(transmitTryCounter > 10) throw new RuntimeException("More than 10 retransmission failures!");
            }
            while ( ! listenTo(SUCCESSFUL_SINGLE_MESSAGE_SYNC) );
            //repeat retransmitting until an OK is received from the other end
        }
        say(END_MESSAGES_SYNC);
    }
    private Map<String, String> listenTo_messageList_syncformat(){
        Map<String, String> msgs = new HashMap<>();

        boolean endOfMessageSync = false;
        while( ! endOfMessageSync){

            final String beginLine = listen();
            String messageHash = null;
            if(beginLine.startsWith(BEGIN_SINGLE_MESSAGE_SYNC)) {
                messageHash = beginLine.split("[ \\t]+")[1];
            } else if (beginLine.equals(END_MESSAGES_SYNC)) {
                endOfMessageSync = true;
                break;
            }

            if(messageHash == null) {
                Log.d(TAG, "Something wrong with other side's message hash (null or bad request line)!");
                say(FAILED_RETX_PLEASE_SINGLE_MESSAGE_SYNC);
                continue; //retry and listen for next message which should be this one repeated
            }

            final String urlEncodedMessage = listen();
            final String message = Encoding.urlDecode(urlEncodedMessage);

            String endLine = listen();
            if(endLine.equals(END_SINGLE_MESSAGE_SYNC)){
                msgs.put(messageHash, message);
                say(SUCCESSFUL_SINGLE_MESSAGE_SYNC);
            }

        }
        return msgs;
    }




    private void say_MessageStoreHashes(){

    }

    private String[] exchangeMessageStoreHashes(){

        listenTo(BEGIN_MESSAGEHASH_SYNC);

        boolean finishedSyncingMessages = false;
        String messageHash;

        while( ! finishedSyncingMessages ){
            String line = listen();

            if(line.equals(END_MESSAGEHASH_SYNC)){
                finishedSyncingMessages = true;
                break;
            }
            else if (line.startsWith(BEGIN_SINGLE_MESSAGE_SYNC)){
                messageHash = line.split("\\s")[1];
                continue;
            } else {
                StringBuilder messageContentsSb = new StringBuilder();
                while ( ! line.equals(END_SINGLE_MESSAGE_SYNC)){
                    messageContentsSb.append(line);
                }
            }
        }

        return null;


    }



    private boolean hello_clientSide(){
        say(HELLO);
        return listenTo(HELLO);
    }
    private boolean hello_serverSide(){

        boolean hello_ok = listenTo(HELLO);

        if(hello_ok) {
            say(HELLO);
        }

        return hello_ok;
    }

    private void say_MessageDump() {
        String messageHashDump = getMessageHashDump();

        say(BEGIN_MESSAGEHASH_SYNC);
        say(messageHashDump);
        say(END_MESSAGEHASH_SYNC);
    }
    private String[] listenTo_MessageDump(){
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
    private static Map<String, String> getFilteredMap(Map<String, String> map, List<String> retainedKeys){
        Map<String, String> out = new HashMap<>();

        for(String k : retainedKeys){
            out.put(k, map.get(k));
        }

        return out;

    }

    private void say(String text){
        //TODO!
    }
    private String listen(){
        //TODO!
        return null;
    }
    private boolean listenTo(String expectedReply){
        return listen().equals(expectedReply);
    }
}
