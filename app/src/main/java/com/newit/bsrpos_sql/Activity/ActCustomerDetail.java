package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.R;

public class ActCustomerDetail extends ActBase {

    private TextView customerdetail_name, customerdetail_addr, customerdetail_ship, customerdetail_tel;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customerdetail);

        if (validate()) {
            customerdetail_name = (TextView) findViewById(R.id.customerdetail_name);
            customerdetail_addr = (TextView) findViewById(R.id.customerdetail_addr);
            customerdetail_ship = (TextView) findViewById(R.id.customerdetail_ship);
            customerdetail_tel = (TextView) findViewById(R.id.customerdetail_tel);

            customerdetail_name.setText(customer.getName());
            customerdetail_addr.setText(String.valueOf(customer.getAddr()));
            customerdetail_ship.setText(String.valueOf(customer.isShip() ? "ส่ง" : "ไม่ส่ง"));
            customerdetail_tel.setText(String.valueOf(customer.getTel()));

            customerdetail_tel.setOnClickListener(v -> {
                if (customer.getTel().equals("")) {
                    return;
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + customerdetail_tel.getText()));
                    startActivity(callIntent);
                }
            });

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("customer", customer);
            Intent intent = new Intent(ActCustomerDetail.this, ActOrderInput.class);
            intent.putExtras(bundle);
            startActivity(intent);
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


}
