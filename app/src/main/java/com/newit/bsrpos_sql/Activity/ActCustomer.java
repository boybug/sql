package com.newit.bsrpos_sql.Activity;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Locale;

public class ActCustomer extends ActBase {

    private List<Customer> customers = new ArrayList<>();
    private List<Customer> backup;
    private String searchString;

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

                if(searchString != null) SetTextSpan(searchString,cus.getName(),cus_name);

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

        ClearSearch(R.id.search_txt, R.id.clear_btn);
        AddVoiceSearch(R.id.search_txt, R.id.search_btn);
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchString = s.toString().toLowerCase(Locale.getDefault());
                List<Customer> filtered = new ArrayList<>();
                for (Customer p : customers) {
                    if (p.getName().toLowerCase(Locale.getDefault()).contains(searchString))
                        filtered.add(p);
                }
                if (backup == null)
                    backup = new ArrayList<>(customers);
                adap.setModels(filtered);
                adap.notifyDataSetChanged();
            }
        });

    }
    public void onBackPressed() {
            Intent intent = new Intent(ActCustomer.this,ActMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            Intent intent = new Intent(ActCustomer.this,ActLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }
}
