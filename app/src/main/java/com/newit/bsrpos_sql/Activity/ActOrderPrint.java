package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.AdpPrint;

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

        AdpCustom<OrderItem> adap = new AdpCustom<OrderItem>(R.layout.listing_grid_orderprint, getLayoutInflater(), order.getItems()) {
            @Override
            protected void populateView(View v, OrderItem model) {
                TextView orderprint_no = (TextView) v.findViewById(R.id.orderprint_no);
                TextView orderprint_desc = (TextView) v.findViewById(R.id.orderprint_desc);
                TextView orderprint_qty = (TextView) v.findViewById(R.id.orderprint_qty);
                orderprint_no.setText(String.valueOf(model.getNo()));
                orderprint_desc.setText(model.getProduct().getName());
                orderprint_qty.setText(String.valueOf(model.getQty()));
            }
        };
        ListView list = (ListView) findViewById(R.id.orderprint_list);
        list.setAdapter(adap);
        AdpPrint.formatListView(list);
        printPDF(order.getNo(), R.id.relativeLayout);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    public void processFinish(ResultSet rs, int tag) throws SQLException {
    }
}
