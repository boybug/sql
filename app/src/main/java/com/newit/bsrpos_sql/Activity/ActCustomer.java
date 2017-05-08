package com.newit.bsrpos_sql.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActCustomer extends ActBase {

    private List<Customer> customers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("รายการลูกค้า@" + Global.wh_name);

        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getcus(" + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                Customer c = new Customer(rs.getInt("cus_Id"), rs.getString("cus_name"), rs.getString("cus_addr"), rs.getString("cus_tel"), rs.getBoolean("cus_ship"));
                customers.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AdpCustom<Customer> adap = new AdpCustom<Customer>(R.layout.listing_grid_cus, getLayoutInflater(), customers) {
            @Override
            protected void populateView(View v, Customer cus) {
                TextView cus_name = (TextView) v.findViewById(R.id.cus_name);
                cus_name.setText(cus.getName());

                TextView cus_addr = (TextView) v.findViewById(R.id.cus_addr);
                cus_addr.setText(cus.getAddr());

                TextView cus_tel = (TextView) v.findViewById(R.id.cus_tel);
                cus_tel.setText(cus.getTel());
            }
        };
        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("customer", customers.get(position));
            Intent intent = new Intent(ActCustomer.this, ActCustomerDetail.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
}
