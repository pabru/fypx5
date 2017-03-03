package com.pandruszkow.fypx5.protocol.message;

import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by piotrek on 24/02/17.
 */

@JsonObject

public class ProtocolMessage {

    @JsonField (typeConverter = TYPEEnumTypeConverter.class)
    @NonNull public TYPE pMsgType;

    //Constant pre-canned messages to avoid regenerating objects needlessly
    private static final ProtocolMessage
            CANNED_HELLO = new ProtocolMessage(TYPE.hello),
            CANNED_BYE = new ProtocolMessage(TYPE.bye);
    private static final ProtocolMessage
            CANNED_REPLY_OK = new ProtocolMessage(TYPE.reply),
            CANNED_REPLY_FAIL = new ProtocolMessage(TYPE.reply);
            static {
                CANNED_REPLY_OK.successful = true;
                CANNED_REPLY_FAIL.successful = false;
            }
    //End constants

    @JsonField
    public List<String> haveMessageHashes;
    @JsonField
    public List<ChatMessage> chatMessages;
    @JsonField
    public Boolean successful;

    protected ProtocolMessage(){
        //don't touch, for serialise/deserialise use only!
    }

    public ProtocolMessage(ProtocolMessage.TYPE type){
        this.pMsgType = type;
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
        msg.haveMessageHashes = hashes;
        return msg;
    }

    public static ProtocolMessage reply(boolean successful){
        if(successful){
            return CANNED_REPLY_OK;
        } else {
            return CANNED_REPLY_FAIL;
        }
    }
    public static ProtocolMessage hello(){
        return CANNED_HELLO;
    }
    public static ProtocolMessage bye(){
        return CANNED_BYE;
    }

    public enum TYPE {
        hello,
        sync_hashes,
        sync_messages,
        reply,
        bye,
    }
    protected static class TYPEEnumTypeConverter extends StringBasedTypeConverter<TYPE> {

        public TYPEEnumTypeConverter(){};

        @Override
        public TYPE getFromString(String string) {
            return TYPE.valueOf(string);
        }

        @Override
        public String convertToString(TYPE object) {
            return object.toString();
        }
    }

}
