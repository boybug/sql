package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.Model.StepPrice;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActProductPrice extends ActBase {

    private Product prod;
    private List<StepPrice> stepPrices = new ArrayList<>();
    private AdpCustom<StepPrice> adap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productprice);

        if (validate()) {
            TextView productprice_name = (TextView) findViewById(R.id.productprice_name);
            TextView productprice_stock = (TextView) findViewById(R.id.productprice_stock);
            TextView productprice_wgt = (TextView) findViewById(R.id.productprice_wgt);

            productprice_name.setText(prod.getName());
            productprice_stock.setText(String.valueOf(prod.getStock()));
            productprice_wgt.setText(String.valueOf(prod.getWeight()));

            new SqlQuery(this, 1, "{call POS.dbo.getstepprice(?,?)}", new String[]{String.valueOf(prod.getId()), String.valueOf(Global.wh_Id)});

            adap = new AdpCustom<StepPrice>(R.layout.listing_grid_productprice, getLayoutInflater(), stepPrices) {
                @Override
                protected void populateView(View v, StepPrice stepPrice) {

                    TextView productprice_from_to = (TextView) v.findViewById(R.id.productprice_from_to);
                    productprice_from_to.setText(stepPrice.getFrom() + " - " + stepPrice.getTo());

                    TextView productprice_price = (TextView) v.findViewById(R.id.productprice_price);
                    productprice_price.setText(String.valueOf(stepPrice.getPrice()));
                }
            };

            ListView list = (ListView) findViewById(R.id.productprice_list_price);
            list.setAdapter(adap);
        }
    }

    private boolean validate() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            prod = (Product) bundle.getSerializable("product");
            if (prod != null)
                return true;
        }
        return false;
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
            stepPrices.clear();
            while (rs != null && rs.next()) {
                StepPrice sp = new StepPrice(rs.getInt("from"), rs.getInt("to"), rs.getFloat("price"));
                stepPrices.add(sp);
            }
            if (adap != null) adap.notifyDataSetChanged();
        }
    }
}
