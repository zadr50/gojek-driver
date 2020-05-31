package com.talagasoft.oc_driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {
    private ChatModel _chat;
    Button btnSend;
    LinearLayout divChat;
    TextView lblChat,lblChatEdit;
    // handle timer schedule mulai jalan ke tujuan
    TimerTask _task;
    final Handler _handler = new Handler();
    Timer _timer;
    String mNomorHp,mNomorHpPenumpang,mNama,mNamaPenumpang;
    SharedPreferences mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        divChat=(LinearLayout)findViewById(R.id.divChat);
        lblChat=(TextView) findViewById(R.id.lblChat);

        lblChatEdit=(TextView)findViewById(R.id.lblText);
        btnSend =(Button) findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        mSetting = getSharedPreferences("setting_gojek", Context.MODE_PRIVATE);

        mNama=mSetting.getString("nama", "Guest");
        mNomorHp=mSetting.getString("no_hp", "0000000000");
        mNomorHpPenumpang=mSetting.getString("no_hp_penumpang", "0000000000");

        _chat=new ChatModel(getBaseContext(),mNomorHp, mNomorHpPenumpang);

        startTimer();

    }
    private void sendMessage() {
        _chat.send(lblChatEdit.getText().toString());
        lblChatEdit.setText("");

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    public void stopTimer() {
        if (_task != null) {
            if(_timer != null ) _timer.cancel();
            _timer=null;
        }
    }
    private void startTimer() {
        if (_timer == null) {
            _timer = new Timer();
        }
        _task = new TimerTask() {
            public void run() {
                _handler.post(new Runnable() {
                    public void run() {
                        lblChat.setText(Html.fromHtml(_chat.refresh()));
                    }
                });
            }
        };

        _timer.schedule(_task,  300, 30000);
    }

}
