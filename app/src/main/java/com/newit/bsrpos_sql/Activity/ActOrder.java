package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

public class ActOrder extends ActBase {

    private List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        hideFloatButton(R.id.fab);
        setTitle("รายการบิลขาย@" + Global.wh_name);

        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getorder(" + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                Order o = new Order(rs.getInt("order_Id"), rs.getString("order_no"), rs.getDate("order_date"),
                        rs.getInt("cus_id"), rs.getString("cus_name"), rs.getInt("wh_id"), OrderStat.valueOf(rs.getString("order_stat")),
                        rs.getInt("qty"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getInt("usr_Id"), rs.getString("usr_name"));
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

                TextView order_qty = (TextView) v.findViewById(R.id.order_qty);
                order_qty.setText(order.getQty());

                TextView order_usr = (TextView) v.findViewById(R.id.order_usr);
                order_usr.setText("ผู้เปิดบิล : " + String.valueOf(order.getUsr_name()));

                if (order.getStat().equals("new")) {
                    v.setBackgroundColor(Color.parseColor("#abdacf"));
                    order_amount.setTextColor(Color.parseColor("#ff0000"));
                    order_cus.setTextColor(Color.parseColor("#0070a2"));
                } else if (order.getStat().equals("confirm")) {
                    v.setBackgroundColor(Color.parseColor("#F49144"));
                    order_amount.setTextColor(Color.parseColor("#000000"));
                    order_cus.setTextColor(Color.parseColor("#000000"));
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

    }
}
