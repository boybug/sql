package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.R;

import java.sql.ResultSet;

public class ActCustomerDetail extends ActBase {

    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customerdetail);

        if (validate()) {
            TextView customerdetail_name = (TextView) findViewById(R.id.customerdetail_name);
            TextView customerdetail_addr = (TextView) findViewById(R.id.customerdetail_addr);
            TextView customerdetail_ship = (TextView) findViewById(R.id.customerdetail_ship);
            final TextView customerdetail_tel = (TextView) findViewById(R.id.customerdetail_tel);

            customerdetail_name.setText(customer.getName());
            customerdetail_addr.setText(String.valueOf(customer.getAddr()));
            customerdetail_ship.setText(String.valueOf(customer.isShip() ? "ส่ง" : "ไม่ส่ง"));
            customerdetail_tel.setText(String.valueOf(customer.getTel()));

            customerdetail_tel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!customer.getTel().equals("")) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + customerdetail_tel.getText()));
                        ActCustomerDetail.this.startActivity(callIntent);
                    }
                }
            });

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("customer", customer);
                Intent intent = new Intent(ActCustomerDetail.this, ActOrderInput.class);
                intent.putExtras(bundle);
                ActCustomerDetail.this.startActivity(intent);
            }
        });
    }

    private boolean validate() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            customer = (Customer) bundle.getSerializable("customer");
            if (customer != null)
                return true;
        }
        return false;
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
    public void processFinish(ResultSet rs, int tag) {
    }
}
