package com.pandruszkow.fypx5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class NoticeBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);
    }

    public void onClick_postNewNotice(View v){
        Intent postNewI = new Intent(this, PostNoticeActivity.class);
        startActivity(postNewI);
    }
}
