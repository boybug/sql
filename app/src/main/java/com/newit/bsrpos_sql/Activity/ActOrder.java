package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActOrder extends ActBase {

    private List<Order> orders = new ArrayList<>();
    private List<Order> backup;
    private String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("รายการบิลขาย@" + Global.wh_name);

        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getorder(" + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                Order o = new Order(rs.getInt("id"), rs.getString("no"), rs.getString("order_date"),
                        rs.getInt("cus_id"), rs.getString("cus_name"), rs.getInt("wh_id"), OrderStat.valueOf(rs.getString("order_stat")),
                        rs.getInt("qty"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getInt("usr_id"), rs.getString("usr_name"),
                        rs.getString("pay"), rs.getBoolean("ship"), rs.getString("remark"));
                orders.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AdpCustom<Order> adap = new AdpCustom<Order>(R.layout.listing_grid_order, getLayoutInflater(), orders) {
            @Override
            protected void populateView(View v, Order order) {

                TextView order_no = (TextView) v.findViewById(R.id.order_no);
                order_no.setText(order.getNo());

                TextView order_cus = (TextView) v.findViewById(R.id.order_cus);
                order_cus.setText(order.getCus_name());

                TextView order_amount = (TextView) v.findViewById(R.id.order_amount);
                order_amount.setText(String.valueOf(order.getAmount()));

                TextView order_ship = (TextView) v.findViewById(R.id.order_ship);
                order_ship.setText(String.valueOf(order.isShip() ? "ส่ง" : "ไม่ส่ง"));

                TextView order_usr = (TextView) v.findViewById(R.id.order_usr);
                order_usr.setText("ผู้เปิดบิล : " + String.valueOf(order.getUsr_name()));

                if (order.getStat().toString()== "New") {
                    v.setBackgroundColor(Color.parseColor("#abdacf"));
                    order_amount.setTextColor(Color.parseColor("#ff0000"));
                    order_cus.setTextColor(Color.parseColor("#0070a2"));
                } else if (order.getStat().toString()=="Confirm") {
                    v.setBackgroundColor(Color.parseColor("#F49144"));
                    order_amount.setTextColor(Color.parseColor("#000000"));
                    order_cus.setTextColor(Color.parseColor("#000000"));
                }

                if(searchString != null)
                {
                    SetTextSpan(searchString,order.getNo(),order_no);
                    SetTextSpan(searchString,order.getCus_name(),order_cus);
                }
            }
        };
        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener((parent, view, position, id) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("order", orders.get(position));
            Intent intent = new Intent(ActOrder.this, ActOrderInput.class);
            intent.putExtras(bundle);
            startActivity(intent);
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
                List<Order> filtered = new ArrayList<>();
                for (Order o : orders) {
                    if (o.getNo().contains(searchString))
                        filtered.add(o);
                }
                if (backup == null)
                    backup = new ArrayList<>(orders);
                adap.setModels(filtered);
                adap.notifyDataSetChanged();
            }
        });

    }
}
