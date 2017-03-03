package com.pandruszkow.fypx5.protocol;

import com.pandruszkow.fypx5.protocol.message.ChatMessage;
import com.pandruszkow.fypx5.protocol.message.ProtocolMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piotrek on 03/03/17.
 */

public abstract class Protocol {

    //stores messages we know about and have on the device
    protected static Map<String, ChatMessage> messageStore = new HashMap<>();
    public static void storeMessage(ChatMessage msg) {
        messageStore.put(msg.messageHash, msg);
    }

    public abstract ProtocolMessage receive(ProtocolMessage pM);

    protected Map<String, ChatMessage> ourHashesNotInTheirHashes(List<String> theirHashes) {
        //filter to find which of our messages client is missing, i.e. in our store but not in their store.
        List<String> ourHashes = new ArrayList<>(messageStore.keySet());
        List<String> messagesToSend_hashes = new ArrayList<>();

        messagesToSend_hashes.addAll(ourHashes);
        messagesToSend_hashes.removeAll(theirHashes);

        return filterMap(messagesToSend_hashes, messageStore);
    }
    protected static Map<String, ChatMessage> filterMap(List<String> retainFilter, Map<String, ChatMessage> map){
        Map<String, ChatMessage> out = new HashMap<>();

        for(String k : retainFilter){
            out.put(k, map.get(k));
        }
        return out;
    }
}
