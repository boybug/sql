package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.newit.bsrpos_sql.R;

import java.sql.ResultSet;

public class ActSplash extends ActBase {

    private Handler handler;
    private Runnable runnable;
    private long delay_time;
    private long time = 3000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        hideActionBar();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ActSplash.this, ActLogin.class);
                ActSplash.this.startActivity(intent);
                ActSplash.this.finish();
            }
        };
    }

    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) {

    }
}
