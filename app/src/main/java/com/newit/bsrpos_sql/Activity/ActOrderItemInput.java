package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.R;

public class ActOrderItemInput extends ActBase {

    private TextView orderiteminput_desc, orderiteminput_stock, orderiteminput_wgt, orderiteminput_price, orderiteminput_amt;
    private EditText orderiteminput_qty;
    private OrderItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderiteminput);

        if (validate()) {
            orderiteminput_desc = (TextView) findViewById(R.id.orderiteminput_desc);
            orderiteminput_stock = (TextView) findViewById(R.id.orderiteminput_stock);
            orderiteminput_qty = (EditText) findViewById(R.id.orderiteminput_qty);
            orderiteminput_wgt = (TextView) findViewById(R.id.orderiteminput_wgt);
            orderiteminput_price = (TextView) findViewById(R.id.orderiteminput_price);
            orderiteminput_amt = (TextView) findViewById(R.id.orderiteminput_amt);
            Button orderiteminput_decr = (Button) findViewById(R.id.orderiteminput_decr);
            Button orderiteminput_incr = (Button) findViewById(R.id.orderiteminput_incr);

            orderiteminput_qty.setSelectAllOnFocus(true);
            redraw();

            orderiteminput_decr.setOnClickListener(v -> {
                item.addQty(-1);
                redraw();
            });

            orderiteminput_incr.setOnClickListener(v -> {
                item.addQty(1);
                redraw();
            });

            Button orderinput_save = (Button) findViewById(R.id.orderinput_save);
            orderinput_save.setOnClickListener(v -> {
                item.save();
            });


        }
    }

    private boolean validate() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            item = (OrderItem) bundle.getSerializable("orderItem");
            if (item != null)
                return true;
        }
        return false;
    }

    private void redraw() {
        orderiteminput_desc.setText(item.getProduct().getName());
        orderiteminput_stock.setText(String.valueOf(item.getProduct().getStock()));
        orderiteminput_qty.setText(String.valueOf(item.getQty()));
        orderiteminput_wgt.setText(String.valueOf(item.getWeight()));
        orderiteminput_price.setText(String.valueOf(item.getPrice()));
        orderiteminput_amt.setText(String.valueOf(item.getAmount()));
        orderiteminput_qty.clearFocus();
    }
}
