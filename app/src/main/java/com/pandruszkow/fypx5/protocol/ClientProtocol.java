package com.pandruszkow.fypx5.protocol;

import android.util.Log;

import com.pandruszkow.fypx5.protocol.message.ChatMessage;
import com.pandruszkow.fypx5.protocol.message.ProtocolMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrek on 23/02/17.
 */
public class ClientProtocol extends Protocol {

    private final static String TAG = ClientProtocol.class.getCanonicalName();

    //where in the protocol progress we are
    private STATE protoState = STATE.BEGIN;

    private List<String> theirHashes;
    public ProtocolMessage receive(ProtocolMessage pM) {

        Log.d(TAG, "received following ProtocolMessage: " + pM.toString());
        ProtocolMessage.TYPE type = (pM != null) ? pM.pMsgType : null;

        switch (protoState) {
            case BEGIN:
                protoState = STATE.ACCEPT_HELLO_SEND_OUR_SYNC_HASHES;
                return ProtocolMessage.hello();
            case ACCEPT_HELLO_SEND_OUR_SYNC_HASHES:
                if(type == ProtocolMessage.TYPE.hello) {
                    protoState = STATE.ACCEPT_HASHES_SEND_OUR_MESSAGES;
                    return ProtocolMessage.sync_hashes(new ArrayList<>(messageStore.keySet()));
                } else {
                    return null;
                }
            case ACCEPT_HASHES_SEND_OUR_MESSAGES:
                if(type == ProtocolMessage.TYPE.sync_hashes){
                    theirHashes = pM.haveMessageHashes;
                    protoState = STATE.ACCEPT_THEIR_MESSAGES_SEND_BYE;
                    return ProtocolMessage.sync_messages(ourHashesNotInTheirHashes(theirHashes));
                } else {
                    return null;
                }
            case ACCEPT_THEIR_MESSAGES_SEND_BYE:
                if(type == ProtocolMessage.TYPE.sync_messages) {
                    for(ChatMessage cM : pM.chatMessages){
                        messageStore.put(cM.messageHash, cM);
                    }
                    protoState = STATE.READY_FOR_BYE;
                    return ProtocolMessage.bye();
                } else {
                    return null;
                }
            case READY_FOR_BYE:
                if (type == ProtocolMessage.TYPE.bye) {
                    protoState = STATE.END;
                    return ProtocolMessage.bye();
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    public enum STATE {
        BEGIN,
        ACCEPT_HELLO_SEND_OUR_SYNC_HASHES,
        ACCEPT_HASHES_SEND_OUR_MESSAGES,
        ACCEPT_THEIR_MESSAGES_SEND_BYE,
        READY_FOR_BYE,
        END
    }

}
