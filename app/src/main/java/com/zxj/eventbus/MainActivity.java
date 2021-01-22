package com.zxj.eventbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zxj.eventbus.annotation.Subscribe;
import com.zxj.eventbus.eventbus.EventBus;
import com.zxj.eventbus.interfaces.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("123");
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void getEventBus(String receiverString){
        Log.d("================>",receiverString);
        Log.d("================>",Thread.currentThread().getName());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}