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
import com.newit.bsrpos_sql.Model.WhGrp;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActWhGrp extends ActBase {

    private List<WhGrp> whGrps = new ArrayList<>();
    private AdpCustom<WhGrp> adap;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("โปรดเลือกคลัง");
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<WhGrp>(R.layout.listing_grid_whgrp, getLayoutInflater(), whGrps) {
            @Override
            protected void populateView(View v, WhGrp wh) {
                TextView wh_grp_name = (TextView) v.findViewById(R.id.wh_grp_name);
                wh_grp_name.setText(wh.getName());

                if (searchString != null) SetTextSpan(searchString, wh.getName(), wh_grp_name);
            }
        };
        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WhGrp whGrp = adap.getItem(position);
                Global.wh_Grp_Id = whGrp.getId();
                Global.wh_grp_name = whGrp.getName();
                Intent intent = new Intent(ActWhGrp.this, ActMain.class);
                ActWhGrp.this.startActivity(intent);
                ActWhGrp.this.finish();
            }
        });
        refresh();

        addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, whGrps, adap);
    }

    public void onBackPressed() {
        backPressed(ActLogin.class, "ออกจากระบบ", "ท่านต้องการออกจากระบบหรือไม่?");
    }

    @Override
    public void refresh() {
        new SqlQuery(this, 1, "{call " + Global.database.getPrefix() + "getwhgrp(?)}", new String[]{String.valueOf(Global.user.getId())});
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
            whGrps.clear();
            while (rs != null && rs.next()) {
                WhGrp w = new WhGrp(rs.getInt("wh_Grp_Id"), rs.getString("wh_grp_name"));
                whGrps.add(w);
            }
            if (adap != null) adap.notifyDataSetChanged();
        }
    }
}
