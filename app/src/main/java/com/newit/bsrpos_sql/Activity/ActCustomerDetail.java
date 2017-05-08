package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.Model.StepPrice;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActCustomerDetail extends ActBase {

    private TextView customerdetail_name, customerdetail_addr, customerdetail_ship, customerdetail_tel;
    private Customer cus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customerdetail);

        if (validate()) {
            customerdetail_name = (TextView) findViewById(R.id.customerdetail_name);
            customerdetail_addr = (TextView) findViewById(R.id.customerdetail_addr);
            customerdetail_ship = (TextView) findViewById(R.id.customerdetail_ship);
            customerdetail_tel = (TextView) findViewById(R.id.customerdetail_tel);

            customerdetail_name.setText(cus.getName());
            customerdetail_addr.setText(String.valueOf(cus.getAddr()));
            customerdetail_ship.setText(String.valueOf(cus.isShip() ? "ส่ง" : "ไม่ส่ง"));
            customerdetail_tel.setText(String.valueOf(cus.getTel()));

            customerdetail_tel.setOnClickListener(v -> {
                // TODO Auto-generated method stub
                if(cus.getTel().equals("")) {
                    return;
                }
                else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + customerdetail_tel.getText()));
                    startActivity(callIntent);
                }
            });

        }
    }

    private boolean validate() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cus = (Customer) bundle.getSerializable("customer");
            if (cus != null)
                return true;
        }
        return false;
    }


}
