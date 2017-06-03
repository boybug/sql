package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.newit.bsrpos_sql.Model.FbStock;
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
    private Query fb;
    private ChildEventListener fbStockListner;
    private TextView productprice_stock, productprice_fb, productprice_remaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productprice);

        setTitle("รายละเอียดสินค้า@" + Global.wh_grp_name);

        if (validate()) {
            fb = FirebaseDatabase.getInstance().getReference().child(Global.getFbStockPath());

            TextView productprice_name = (TextView) findViewById(R.id.productprice_name);
            productprice_stock = (TextView) findViewById(R.id.productprice_stock);
            productprice_fb = (TextView) findViewById(R.id.productprice_fb);
            productprice_remaining = (TextView) findViewById(R.id.productprice_remaining);
            TextView productprice_wgt = (TextView) findViewById(R.id.productprice_wgt);

            productprice_name.setText(prod.getName());
            productprice_stock.setText(String.valueOf(prod.getStock()));
            productprice_fb.setText("0");
            productprice_remaining.setText(String.valueOf(prod.getStock()));
            productprice_wgt.setText(String.valueOf(prod.getWeight()));

            new SqlQuery(ActProductPrice.this, 1, "{call " + Global.database.getPrefix() + "getstepprice(?,?)}", new String[]{String.valueOf(prod.getId()), String.valueOf(prod.getWh_Id())});

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

            fbStockListner = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    redraw(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    redraw(dataSnapshot);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };

            fb.addChildEventListener(fbStockListner);
        }
    }

    private void redraw(DataSnapshot dataSnapshot) {
        FbStock f = dataSnapshot.getValue(FbStock.class);
        if (f.getWh_id() == prod.getWh_Id() && f.getProd_id() == prod.getId()) {
            prod.setFbstock(f);
            productprice_stock.setText(String.valueOf(prod.getStock()));
            productprice_fb.setText(String.valueOf(prod.getFbstock().getReserve()));
            productprice_remaining.setText(String.valueOf(prod.getRemaining()));
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
    public void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException {
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
