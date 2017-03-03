package com.pandruszkow.fypx5.protocol.proto;

import android.util.Log;

import com.pandruszkow.fypx5.protocol.message.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrek on 23/02/17.
 */
public class ServerProtocol extends Protocol {

    private final static String TAG = ServerProtocol.class.getCanonicalName();


    //where in the protocol progress we are
    private STATE protoState = STATE.READY_FOR_HELLO;

    private List<String> theirHashes;
    public ProtocolMessage receive(ProtocolMessage pM) {

        Log.d(TAG, "received following ProtocolMessage: " + pM.toString());
        ProtocolMessage.TYPE type = pM.pMsgType;

        switch (protoState) {
            case READY_FOR_HELLO:
                if(type == ProtocolMessage.TYPE.hello) {
                    protoState = STATE.READY_FOR_SYNC_HASH;
                    return ProtocolMessage.hello();
                } else {
                    return null;
                }
            case READY_FOR_SYNC_HASH:
                if(type == ProtocolMessage.TYPE.sync_hashes){
                    theirHashes = pM.haveMessageHashes;
                    protoState = STATE.READY_FOR_SYNC_MSGS;
                    return ProtocolMessage.sync_hashes(new ArrayList<>(messageStore.keySet()));
                } else {
                    return null;
                }
            case READY_FOR_SYNC_MSGS:
                if(type == ProtocolMessage.TYPE.sync_messages) {
                    for(ChatMessage cM : pM.chatMessages){
                        messageStore.put(cM.messageHash, cM);
                    }
                    protoState = STATE.READY_FOR_BYE;
                    return ProtocolMessage.sync_messages(
                            ourHashesNotInTheirHashes(theirHashes));
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
        READY_FOR_HELLO,
        READY_FOR_SYNC_HASH,
        READY_FOR_SYNC_MSGS,
        READY_FOR_BYE,
        END
    }


}
