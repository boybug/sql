package com.newit.bsrpos_sql.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.Model.Warehouse;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActWarehouse extends ActBase {

    private List<Warehouse> warehouses = new ArrayList<>();
    private List<Warehouse> backup;
    private String searchString;

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

                if(searchString != null)SetTextSpan(searchString,wh.getName(),wh_name);
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
                List<Warehouse> filtered = new ArrayList<>();
                for (Warehouse w : warehouses) {
                    if (w.getName().toLowerCase(Locale.getDefault()).contains(searchString))
                        filtered.add(w);
                }
                if (backup == null)
                    backup = new ArrayList<>(warehouses);
                adap.setModels(filtered);
                adap.notifyDataSetChanged();
            }
        });
    }

    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ออกจากระบบ");
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setCancelable(true);
        dialog.setMessage("คุณต้องการออกจากระบบ?");
        dialog.setPositiveButton("ใช่", (dialog12, which) -> {

            Intent intent = new Intent(ActWarehouse.this,ActLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        dialog.setNegativeButton("ไม่", (dialog1, which) -> dialog1.cancel());

        dialog.show();

    }
}
