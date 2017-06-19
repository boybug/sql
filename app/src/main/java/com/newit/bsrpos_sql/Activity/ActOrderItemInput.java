package com.newit.bsrpos_sql.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.R;

import java.sql.ResultSet;

public class ActOrderItemInput extends ActBase {

    private TextView orderiteminput_stock;
    private TextView orderiteminput_amt;
    private EditText orderiteminput_qty;
    private OrderItem item;
    private int oldQty;
    private DatabaseReference fb;
    private ValueEventListener fbStockListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderiteminput);

        if (validate()) {
            fb = FirebaseDatabase.getInstance().getReference().child(Global.getFbStockPath(getApplicationContext())).child(item.getProduct().getFbstock().getKey()).child("reserve");

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
            orderiteminput_price.setText(Global.formatMoney(item.getPrice()));

            orderiteminput_qty.setSelectAllOnFocus(true);
            orderiteminput_qty.setText(String.valueOf(item.getQty()));
            orderiteminput_stock.setText(String.valueOf(item.getProduct().getRemaining() + item.getQty()));
            orderiteminput_amt.setText(Global.formatMoney(item.getAmount()));

            oldQty = getQty();

            orderiteminput_decr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getQty() > 0)
                        setQty(getQty() - 1);
                }
            });

            orderiteminput_incr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setQty(getQty() + 1);
                }
            });

            Button orderinput_save = (Button) findViewById(R.id.orderinput_save);
            orderinput_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getQty() <= 0) orderiteminput_qty.setError("จำนวนต้องไม่น้อยกว่า 1");
                    else if (getStock() < getQty()) orderiteminput_qty.setError("สต็อกไม่พอ");
                    else {
                        item.setQty(getQty());
                        Intent intent = new Intent();
                        intent.putExtra("DELTA", getQty() - oldQty);
                        setResult(Activity.RESULT_OK, intent);
                        fb.setValue(item.getProduct().getFbstock().getReserve() + getQty() - oldQty);
                        finish();
                    }
                }
            });


            fbStockListner = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    item.getProduct().getFbstock().setReserve(dataSnapshot.getValue(Integer.class));
                    orderiteminput_stock.setText(String.valueOf(item.getProduct().getRemaining() + item.getQty()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            fb.addValueEventListener(fbStockListner);
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

    private int getQty() {
        if (orderiteminput_qty.length() == 0) orderiteminput_qty.setText("0");
        return Integer.parseInt(orderiteminput_qty.getText().toString());
    }

    private void setQty(int qty) {
        orderiteminput_qty.setText(String.valueOf(qty));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nologout, menu);
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
    public void queryReturn(ResultSet rs, int tag, Object caller) {
    }
}
