package com.newit.bsrpos_sql.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;

import java.util.ArrayList;
import java.util.List;

public class ActOrder extends ActBase {

    private List<Order> orders = new ArrayList<>();
    private AdpCustom<Order> adap;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        setTitle("รายการบิลขาย@" + Global.wh_name);
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
                bundle.putSerializable("order", orders.get(position));
                Intent intent = new Intent(ActOrder.this, ActOrderInput.class);
                intent.putExtras(bundle);
                ActOrder.this.startActivity(intent);
            }
        });
        if (Global.user.isDeleteorder()) {
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final Order order = orders.get(position);
                    if (order.getStat() == OrderStat.Confirm)
                        MessageBox("เอกสารยืนยันแล้วลบไม่ได้");
                    else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ActOrder.this);
                        dialog.setTitle("ยืนยันการลบ");
                        dialog.setIcon(R.mipmap.ic_launcher);
                        dialog.setCancelable(true);
                        dialog.setMessage("ท่านต้องการลบรายการนี้หรือไม่?");
                        dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog12, int which) {
                                SqlResult result = order.delete();
                                MessageBox(result.getMsg() == null ? "ลบเอกสารแล้ว" : result.getMsg());
                                refresh();
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
                intent.putExtra("bypasscustomer", true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            Intent intent = new Intent(ActOrder.this, ActLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void refresh() {
        orders = Order.retrieve(orders);
        if (adap != null) adap.notifyDataSetChanged();
    }
}
