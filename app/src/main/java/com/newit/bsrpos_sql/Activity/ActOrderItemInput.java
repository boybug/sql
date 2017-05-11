package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.Model.RecordStat;
import com.newit.bsrpos_sql.R;

import java.sql.ResultSet;

public class ActOrderItemInput extends ActBase {

    private TextView orderiteminput_stock;
    private TextView orderiteminput_amt;
    private EditText orderiteminput_qty;
    private OrderItem item;
    private int oldQty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderiteminput);

        if (validate()) {
            TextView orderiteminput_desc = (TextView) findViewById(R.id.orderiteminput_desc);
            orderiteminput_stock = (TextView) findViewById(R.id.orderiteminput_stock);
            orderiteminput_qty = (EditText) findViewById(R.id.orderiteminput_qty);
            TextView orderiteminput_wgt = (TextView) findViewById(R.id.orderiteminput_wgt);
            TextView orderiteminput_price = (TextView) findViewById(R.id.orderiteminput_price);
            orderiteminput_amt = (TextView) findViewById(R.id.orderiteminput_amt);
            Button orderiteminput_decr = (Button) findViewById(R.id.orderiteminput_decr);
            Button orderiteminput_incr = (Button) findViewById(R.id.orderiteminput_incr);

            orderiteminput_desc.setText(item.getProduct().getName());
            orderiteminput_wgt.setText(String.valueOf(item.getWeight()));
            orderiteminput_price.setText(String.valueOf(item.getPrice()));

            orderiteminput_qty.setSelectAllOnFocus(true);
            redraw(item.getQty(), item.getProduct().getStock(), item.getAmount(), false);
            oldQty = getQty();

            orderiteminput_decr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getQty() > 0)
                        addQty(-1, false);
                }
            });

            orderiteminput_incr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getStock() >= 1)
                        addQty(1, false);
                }
            });

            Button orderinput_save = (Button) findViewById(R.id.orderinput_save);
            orderinput_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int delta = getQty() - oldQty;
                    if (getStock() >= delta) {
                        item.setQty(getQty());
                        Intent intent = new Intent();
                        intent.putExtra("DELTA", delta);
                        setResult(3, intent);
                        finish();
                    } else orderiteminput_qty.setError("สต็อกไม่พอ");
                }
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


    private int getStock() {
        return Integer.parseInt(orderiteminput_stock.getText().toString());
    }

    private void setStock(int stock) {
        orderiteminput_stock.setText(String.valueOf(stock));
    }

    private int getQty() {
        if (orderiteminput_qty.length() == 0) orderiteminput_qty.setText("0");
        return Integer.parseInt(orderiteminput_qty.getText().toString());
    }

    private void setQty(int qty) {
        orderiteminput_qty.setText(String.valueOf(qty));
    }

    private float getAmount() {
        return Float.parseFloat(orderiteminput_amt.getText().toString());
    }

    private void setAmount(int amount) {
        orderiteminput_amt.setText(String.valueOf(amount));
    }

    private void addQty(int delta, boolean fromTextChangeListener) {
        if (getStock() >= delta) {
            setStock(getStock() - delta);
            if (!fromTextChangeListener)
                setQty(getQty() + delta);
            redraw(getQty(), getStock(), getQty() * item.getPrice(), fromTextChangeListener);
        }
    }

    private void redraw(int qty, int stock, float amount, boolean fromTextChangeListener) {
        if (!fromTextChangeListener)
            orderiteminput_qty.setText(String.valueOf(qty));
        orderiteminput_stock.setText(String.valueOf(stock));
        orderiteminput_amt.setText(String.valueOf(amount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            super.backPressed(ActLogin.class);
        }
        return true;
    }

    @Override
    public void processFinish(ResultSet rs, int tag) {
    }
}
