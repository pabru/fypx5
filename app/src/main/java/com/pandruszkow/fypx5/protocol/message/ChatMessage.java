package com.pandruszkow.fypx5.protocol.message;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

/**
 * Created by piotrek on 25/02/17.
 */

@JsonObject

public class ChatMessage {

    public String messageHash;
    public String author;
    public Date created = new Date();
    public String body;
}
