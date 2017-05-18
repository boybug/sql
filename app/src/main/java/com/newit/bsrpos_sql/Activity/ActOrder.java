package com.newit.bsrpos_sql.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderPay;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActOrder extends ActBase {

    private List<Order> orders = new ArrayList<>();
    private AdpCustom<Order> adap;
    private final int spQuery = 1;
    private final int spDelete = 2;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        setTitle("รายการบิลขาย@" + Global.wh_grp_name);
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<Order>(R.layout.listing_grid_order, getLayoutInflater(), orders) {
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

                if (order.getStat() == OrderStat.New) {
                    v.setBackgroundColor(Color.parseColor("#abdacf"));
                    order_amount.setTextColor(Color.parseColor("#ff0000"));
                    order_cus.setTextColor(Color.parseColor("#0070a2"));
                } else if (order.getStat() == OrderStat.Confirm) {
                    v.setBackgroundColor(Color.parseColor("#F49144"));
                    order_amount.setTextColor(Color.parseColor("#000000"));
                    order_cus.setTextColor(Color.parseColor("#000000"));
                }

                if (searchString != null) {
                    SetTextSpan(searchString, order.getNo(), order_no);
                    SetTextSpan(searchString, order.getCus_name(), order_cus);
                }
            }
        };
        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", adap.getModels().get(position));
                Intent intent = new Intent(ActOrder.this, ActOrderInput.class);
                intent.putExtras(bundle);
                ActOrder.this.startActivity(intent);
            }
        });
        if (Global.user.isDeleteorder()) {
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final Order order = adap.getModels().get(position);
                    if (order.getStat() == OrderStat.Confirm)
                        MessageBox("เอกสารยืนยันแล้วลบไม่ได้");
                    else {
                        AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new AlertDialog.Builder(ActOrder.this, android.R.style.Theme_Material_Light_Dialog_Alert) : new AlertDialog.Builder(ActOrder.this);
                        dialog.setTitle("ยืนยันการลบ");
                        dialog.setIcon(R.mipmap.ic_launcher);
                        dialog.setCancelable(true);
                        dialog.setMessage("ท่านต้องการลบรายการนี้หรือไม่?");
                        dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog12, int which) {
                                new SqlQuery(ActOrder.this, spDelete, "{call " + Global.database.getPrefix() + "deleteorder(?)}", new String[]{String.valueOf(order.getId())});
                            }
                        });
                        dialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                                dialog1.cancel();
                            }
                        });
                        dialog.show();
                    }
                    return true;
                }
            });
        }

        refresh();
        addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, orders, adap);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActOrder.this, ActCustomer.class);
                ActOrder.this.startActivity(intent);
                ActOrder.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        backPressed(ActMain.class);
    }

    @Override
    public void refresh() {
        new SqlQuery(this, spQuery, "{call " + Global.database.getPrefix() + "getorder(?,?)}", new String[]{String.valueOf(Global.wh_Grp_Id), String.valueOf(Global.user.getId())});
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
        if (tag == spQuery) {
            orders.clear();
            while (rs != null && rs.next()) {
                Order o = new Order(rs.getInt("id"), rs.getString("no"), rs.getString("order_date"),
                        rs.getInt("cus_id"), rs.getString("cus_name"), rs.getInt("wh_grp_id"), OrderStat.valueOf(rs.getString("order_stat")),
                        rs.getInt("qty"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getInt("usr_id"), rs.getString("usr_name"),
                        OrderPay.valueOf(rs.getString("pay")), rs.getBoolean("ship"), rs.getString("remark"));
                orders.add(o);
            }
            if (adap != null) adap.notifyDataSetChanged();
        } else if (tag == spDelete) {
            if (rs != null && rs.next()) {
                SqlResult result = new SqlResult(rs);
                MessageBox(result.getMsg() == null ? "ลบเอกสารแล้ว" : result.getMsg());
                refresh();
            }
        }
    }
}
