package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Warehouse;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActWarehouse extends ActBase {

    private List<Warehouse> warehouses = new ArrayList<>();
    private AdpCustom<Warehouse> adap;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("โปรดเลือกคลัง");
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<Warehouse>(R.layout.listing_grid_wh, getLayoutInflater(), warehouses) {
            @Override
            protected void populateView(View v, Warehouse wh) {
                TextView wh_name = (TextView) v.findViewById(R.id.wh_name);
                wh_name.setText(wh.getName());

                if (searchString != null) SetTextSpan(searchString, wh.getName(), wh_name);
            }
        };
        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Warehouse warehouse = adap.getItem(position);
                Global.wh_Id = warehouse.getId();
                Global.wh_name = warehouse.getName();
                Intent intent = new Intent(ActWarehouse.this, ActMain.class);
                ActWarehouse.this.startActivity(intent);
                ActWarehouse.this.finish();
            }
        });
        refresh();

        addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, warehouses, adap);
    }

    public void onBackPressed() {
        backPressed(ActLogin.class, "ออกจากระบบ", "ท่านต้องการออกจากระบบหรือไม่?");
    }

    @Override
    public void refresh() {
        new SqlQuery(this, 1, "{call POS.dbo.getwh(?)}", new String[]{String.valueOf(Global.user.getId())});
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
            warehouses.clear();
            while (rs != null && rs.next()) {
                Warehouse w = new Warehouse(rs.getInt("wh_Id"), rs.getString("wh_name"));
                warehouses.add(w);
            }
            if (adap != null) adap.notifyDataSetChanged();
        }
    }
}
