package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.newit.bsrpos_sql.R;

public class ActSplash extends ActBase {

    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        hideActionBar();

        handler = new Handler();
        runnable = () -> {
            Intent intent = new Intent(ActSplash.this, ActLogin.class);
            startActivity(intent);
            finish();
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
}
