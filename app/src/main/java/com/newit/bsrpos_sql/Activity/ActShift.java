package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Shift;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActShift extends ActBase {

    private List<Shift> shifts = new ArrayList<>();
    private AdpCustom<Shift> adap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        if (!Global.getUser(getApplicationContext()).isAdmin()) hideFloatButton(R.id.fab);
        setTitle("เปิดปิดกะ");
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<Shift>(R.layout.listing_grid_whgrp, getLayoutInflater(), shifts) {
            @Override
            protected void populateView(View v, Shift shift) {
                TextView shift_name = (TextView) v.findViewById(R.id.wh_grp_name);
                shift_name.setText(shift.getName());

                if (searchString != null) SetTextSpan(searchString, shift.getName(), shift_name);
            }
        };
        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Shift shift = adap.getItem(position);
//                Intent intent = new Intent(ActWhGrp.this, ActMain.class);
//                ActWhGrp.this.startActivity(intent);
//                ActWhGrp.this.finish();
            }
        });
        refresh();

        addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, shifts, adap);
    }

    @Override
    public void onBackPressed() {
        super.backPressed(ActMain.class);
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
    public void refresh() {
        new SqlQuery(ActShift.this, 1, "{call " + Global.getDatabase(getApplicationContext()).getPrefix() + "getshift(?)}", new String[]{String.valueOf(Global.getwh_Grp_Id(getApplicationContext()))});

    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException {
        if (tag == 1) {
            shifts.clear();
            while (rs != null && rs.next()) {
                Shift shift = new Shift(rs.getInt("shift_id"), rs.getString("shift_name"), rs.getString("user_name"), rs.getInt("wh_grp_Id"), rs.getString("shift_date"), rs.getBoolean("isClosed"), rs.getString("tel"), rs.getString("imei"));
                shifts.add(shift);
            }
            if (adap != null) adap.notifyDataSetChanged();
        }
    }
}
