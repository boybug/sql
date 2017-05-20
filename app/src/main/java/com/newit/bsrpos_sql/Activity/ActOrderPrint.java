package com.newit.bsrpos_sql.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.newit.bsrpos_sql.Model.Invoice;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.R;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActOrderPrint extends ActBase {

    private Order order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.orderprint);

        hideActionBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        } else order = (Order) bundle.getSerializable("order");

    }

    @Override
    public void processFinish(ResultSet rs, int tag) throws SQLException {
    }
}
