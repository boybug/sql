package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Warehouse;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;

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
        list.setOnItemClickListener((parent, view, position, id) -> {
            Warehouse warehouse = adap.getItem(position);
            Global.wh_Id = warehouse.getId();
            Global.wh_name = warehouse.getName();
            Intent intent = new Intent(ActWarehouse.this, ActMain.class);
            startActivity(intent);
            finish();
        });
        refresh();

        AddVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, warehouses, adap);
    }

    public void onBackPressed() {
        backPressed(ActLogin.class, "ออกจากระบบ", "ท่านต้องการออกจากระบบหรือไม่?");
    }

    @Override
    public void refresh() {
        warehouses = Warehouse.retrieve(warehouses);
        if (adap != null) adap.notifyDataSetChanged();
    }
}
