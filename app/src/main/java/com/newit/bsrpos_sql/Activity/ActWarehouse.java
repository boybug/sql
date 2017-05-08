package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Warehouse;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActWarehouse extends ActBase {

    private List<Warehouse> warehouses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("โปรดเลือกคลัง");

        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getwh(" + Global.usr_Id + ")}");
            while (rs.next()) {
                Warehouse w = new Warehouse(rs.getInt("wh_Id"), rs.getString("wh_name"));
                warehouses.add(w);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AdpCustom<Warehouse> adap = new AdpCustom<Warehouse>(R.layout.listing_grid_wh, getLayoutInflater(), warehouses) {
            @Override
            protected void populateView(View v, Warehouse wh) {
                TextView wh_name = (TextView) v.findViewById(R.id.wh_name);
                wh_name.setText(wh.getName());
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
    }
}
