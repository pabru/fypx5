package com.pandruszkow.fypx5.protocol.message;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

/**
 * Created by piotrek on 25/02/17.
 */

@JsonObject

public class ChatMessage {

    @JsonField public String messageHash;
    @JsonField public String author;
    @JsonField public Date created = new Date();
    @JsonField public String body;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChatMessage{");
        sb.append("messageHash='").append(messageHash).append('\'');
        sb.append(", author='").append(author).append('\'');
        sb.append(", created=").append(created);
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
