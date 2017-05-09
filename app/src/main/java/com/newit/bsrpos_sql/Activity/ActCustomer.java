package com.newit.bsrpos_sql.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;

import java.util.ArrayList;
import java.util.List;

public class ActCustomer extends ActBase {

    private List<Customer> customers = new ArrayList<>();
    private AdpCustom<Customer> adap;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("รายการลูกค้า@" + Global.wh_name);
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<Customer>(R.layout.listing_grid_cus, getLayoutInflater(), customers) {
            @Override
            protected void populateView(View v, Customer cus) {
                TextView cus_name = (TextView) v.findViewById(R.id.cus_name);
                cus_name.setText(cus.getName());

                TextView cus_addr = (TextView) v.findViewById(R.id.cus_addr);
                cus_addr.setText(cus.getAddr());

                TextView cus_tel = (TextView) v.findViewById(R.id.cus_tel);
                cus_tel.setText(cus.getTel());

                if (searchString != null) SetTextSpan(searchString, cus.getName(), cus_name);

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

        refresh();
        AddVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, customers, adap);
    }

    @Override
    public void onBackPressed() {
        backPressed(ActMain.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            Intent intent = new Intent(ActCustomer.this, ActLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void refresh() {
        customers = Customer.retrieve(customers);
        if (adap != null) adap.notifyDataSetChanged();
    }
}
