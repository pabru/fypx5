package com.pandruszkow.fypx5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pandruszkow.fypx5.protocol.Encoding;
import com.pandruszkow.fypx5.protocol.Protocol;
import com.pandruszkow.fypx5.protocol.message.ChatMessage;

public class PostNoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_notice);

        ((Button) findViewById(R.id.post_button)).setOnClickListener((v)->onClick_postNoticeButton(v));
    }

    public void onClick_postNoticeButton(View v){
        ChatMessage msg = new ChatMessage();
        msg.author = ((EditText)findViewById(R.id.author_edit)).getText().toString();
        msg.body = ((EditText)findViewById(R.id.messageBody_edit)).getText().toString();
        msg.messageHash = Encoding.sha256(msg.body);

        Protocol.storeMessage(msg);

        finish();
    }
}
