package com.newit.bsrpos_sql.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;

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
        setTitle("รายการสินค้า@" + Global.wh_name);
        setSwipeRefresh(R.id.swipe_refresh);

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
        list.setOnItemClickListener((parent, view, position, id) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("product", products.get(position));
            Intent intent = new Intent(ActProduct.this, ActProductPrice.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        refresh();
        AddVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, products, adap);
    }

    @Override
    public void onBackPressed() {
        super.backPressed(ActMain.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            Intent intent = new Intent(ActProduct.this, ActLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void refresh() {
        products = Product.retrieve(products);
        if (adap != null) adap.notifyDataSetChanged();
    }
}
