package com.newit.bsrpos_sql.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActProduct extends ActBase {

    private List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("รายการสินค้า@" + Global.wh_name);

        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getproduct(" + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                Product p = new Product(rs.getInt("prod_Id"), rs.getString("prod_name"),
                        rs.getInt("stock"), rs.getFloat("weight"),
                        rs.getString("color"), rs.getBoolean("stepprice"),
                        rs.getFloat("price"), rs.getInt("uom_id"));
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AdpCustom<Product> adap = new AdpCustom<Product>(R.layout.listing_grid_product, getLayoutInflater(), products) {
            @Override
            protected void populateView(View v, Product prod) {
                TextView product_name = (TextView) v.findViewById(R.id.product_name);
                product_name.setText(prod.getName());

                TextView product_price = (TextView) v.findViewById(R.id.product_price);
                product_price.setText(String.valueOf(prod.getPrice()));

                v.setBackgroundColor(Color.parseColor(prod.getColor()));

                if(!prod.getColor().equals("#ffffff")) {
                    product_name.setTextColor(Color.BLACK);
                }
                else {
                    product_name.setTextColor(Color.parseColor("#0070a2"));
                }

                if(prod.isStepPrice()) {
                    product_price.setTextColor(Color.RED);
                }else{
                    product_price.setTextColor(Color.BLACK);
                }
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
    }
}
