package com.newit.bsrpos_sql.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActProduct extends ActBase {

    private List<Product> products = new ArrayList<>();
    private AdpCustom<Product> adap;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("รายการสินค้า@" + Global.wh_grp_name);
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<Product>(R.layout.listing_grid_product, getLayoutInflater(), products) {
            @Override
            protected void populateView(View v, Product prod) {
                TextView product_name = (TextView) v.findViewById(R.id.product_name);
                product_name.setText(prod.getName());

                TextView product_price = (TextView) v.findViewById(R.id.product_price);
                product_price.setText(String.valueOf(prod.getPrice()));

                v.setBackgroundColor(Color.parseColor(prod.getColor()));

                if (!prod.getColor().equals("#ffffff")) {
                    product_name.setTextColor(Color.BLACK);
                } else {
                    product_name.setTextColor(Color.parseColor("#0070a2"));
                }

                if (prod.isStepPrice()) {
                    product_price.setTextColor(Color.RED);
                } else {
                    product_price.setTextColor(Color.BLACK);
                }

                if (searchString != null) SetTextSpan(searchString, prod.getName(), product_name);

            }
        };
        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", adap.getModels().get(position));
                Intent intent = new Intent(ActProduct.this, ActProductPrice.class);
                intent.putExtras(bundle);
                ActProduct.this.startActivity(intent);
            }
        });

        refresh();
        addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, products, adap);
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
        new SqlQuery(ActProduct.this, this, 1, "{call " + Global.database.getPrefix() + "getproduct(?)}", new String[]{String.valueOf(Global.wh_Grp_Id)});

    }

    @Override
    public void processFinish(ResultSet rs, int tag) throws SQLException {
        if (tag == 1) {
            products.clear();
            while (rs != null && rs.next()) {
                Product p = new Product(rs.getInt("prod_Id"), rs.getString("prod_name"), rs.getInt("stock"), rs.getFloat("weight"), rs.getString("color"), rs.getBoolean("stepprice"), rs.getFloat("price"), rs.getInt("uom_id"), rs.getInt("wh_Id"));
                products.add(p);
            }
            if (adap != null) adap.notifyDataSetChanged();
        }
    }
}
