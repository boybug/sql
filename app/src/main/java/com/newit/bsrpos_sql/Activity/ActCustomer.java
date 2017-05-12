package com.newit.bsrpos_sql.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("customer", adap.getModels().get(position));
                Intent intent = new Intent(ActCustomer.this, ActCustomerDetail.class);
                intent.putExtras(bundle);
                ActCustomer.this.startActivity(intent);
            }
        });

        refresh();
        addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, customers, adap);
    }

    @Override
    public void onBackPressed() {
        backPressed(ActMain.class);
    }

    @Override
    public void refresh() {
        new SqlQuery(this, 1, "{call POS.dbo.getcus(?)}", new String[]{String.valueOf(Global.wh_Id)});
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
    public void processFinish(ResultSet rs, int tag) throws SQLException {
        if (tag == 1) {
            customers.clear();
            while (rs != null && rs.next()) {
                Customer c = new Customer(rs.getInt("cus_Id"), rs.getString("cus_name"), rs.getString("cus_addr"), rs.getString("cus_tel"), rs.getBoolean("cus_ship"));
                customers.add(c);
            }
            if (adap != null) adap.notifyDataSetChanged();
        }
    }
}
