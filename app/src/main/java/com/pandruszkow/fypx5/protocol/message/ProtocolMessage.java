package com.pandruszkow.fypx5.protocol.message;

import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.pandruszkow.fypx5.protocol.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by piotrek on 24/02/17.
 */

@JsonObject

public class ProtocolMessage {

    @NonNull public String pMsgType;

    public List<String> requestedMessageHashes;
    public List<ChatMessage> chatMessages;
    public boolean successful;

    public ProtocolMessage(ProtocolMessage.TYPE type){
        this.pMsgType = type.toString();
    }

    public static ProtocolMessage sync_messages(Map<String, ChatMessage> messages){
        ProtocolMessage out = new ProtocolMessage(TYPE.sync_messages);
        out.chatMessages = new ArrayList<>();

        for(String msgHash : messages.keySet()){
            out.chatMessages.add(messages.get(msgHash));
        }

        return out;
    }
    public static ProtocolMessage sync_hashes(List<String> hashes){
        ProtocolMessage msg = new ProtocolMessage(TYPE.sync_hashes);
        msg.requestedMessageHashes = hashes;
        return msg;
    }

    public static ProtocolMessage reply(boolean successful){
        ProtocolMessage out = new ProtocolMessage(TYPE.reply);
        out.successful = successful;

        return out;
    }

    public static ProtocolMessage hello(){
        return new ProtocolMessage(TYPE.hello);
    }
    public static ProtocolMessage bye(){
        return new ProtocolMessage(TYPE.bye);
    }

    public enum TYPE {
        hello,
        sync_hashes,
        sync_messages,
        reply,
        bye,
    }

}
