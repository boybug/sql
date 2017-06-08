package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Invoice;
import com.newit.bsrpos_sql.Model.OrderPay;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActInvoice extends ActBase {

    private List<Invoice> invoices = new ArrayList<>();
    private AdpCustom<Invoice> adap;
    private final int spQuery = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setTitle("รายการใบแจ้งหนี้@" + Global.wh_grp_name);
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<Invoice>(R.layout.listing_grid_invoice, getLayoutInflater(), invoices) {
            @Override
            protected void populateView(View v, Invoice invoice) {

                TextView order_no = (TextView) v.findViewById(R.id.order_no);
                order_no.setText("ใบสั่ง : " + invoice.getOrder_no());

                TextView invoice_no = (TextView) v.findViewById(R.id.invoice_no);
                invoice_no.setText("ใบแจ้งหนี้ : " + invoice.getNo());

                TextView invoice_cus = (TextView) v.findViewById(R.id.invoice_cus);
                invoice_cus.setText(invoice.getCus_name());

                TextView invoice_amount = (TextView) v.findViewById(R.id.invoice_amount);
                invoice_amount.setText(Global.formatMoney(invoice.getAmount()));

                TextView invoice_ship = (TextView) v.findViewById(R.id.invoice_ship);
                invoice_ship.setText(String.valueOf(invoice.isShip() ? "ส่ง" : "ไม่ส่ง"));

                TextView invoice_usr = (TextView) v.findViewById(R.id.invoice_usr);
                invoice_usr.setText("ผู้เปิดใบแจ้งหนี้ : " + String.valueOf(invoice.getUsr_name()));

                if (invoice.getStat() == null) {
                    v.setBackgroundColor(Color.WHITE);
                    invoice_amount.setTextColor(Color.BLACK);
                    invoice_cus.setTextColor(Color.BLACK);
                    order_no.setTextColor(Color.parseColor("#F49144"));
                } else if (invoice.getStat() == OrderStat.New) {
                    v.setBackgroundColor(Color.parseColor("#abdacf"));
                    invoice_amount.setTextColor(Color.parseColor("#ff0000"));
                    invoice_cus.setTextColor(Color.parseColor("#0070a2"));
                    order_no.setTextColor(Color.parseColor("#F49144"));
                } else if (invoice.getStat() == OrderStat.Confirm) {
                    v.setBackgroundColor(Color.parseColor("#F49144"));
                    invoice_amount.setTextColor(Color.parseColor("#000000"));
                    invoice_cus.setTextColor(Color.parseColor("#000000"));
                    order_no.setTextColor(Color.parseColor("#000000"));
                } else if (invoice.getStat() == OrderStat.Cancel) {
                    v.setBackgroundColor(Color.parseColor("#f64343"));
                    invoice_amount.setTextColor(Color.parseColor("#000000"));
                    invoice_cus.setTextColor(Color.parseColor("#000000"));
                    order_no.setTextColor(Color.parseColor("#000000"));
                }

                if (searchString != null) {
                    SetTextSpan(searchString, invoice.getNo(), invoice_no);
                    SetTextSpan(searchString, invoice.getCus_name(), invoice_cus);
                }
            }
        };
        ListView list = (ListView) findViewById(R.id.listing_list);
        list.setAdapter(adap);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("invoice", adap.getModels().get(position));
                Intent intent = new Intent(ActInvoice.this, ActInvoiceInput.class);
                intent.putExtras(bundle);
                ActInvoice.this.startActivity(intent);
            }
        });

        refresh();
        addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, invoices, adap);
        hideFloatButton(R.id.fab);
    }

    @Override
    public void onBackPressed() {
        backPressed(ActMain.class);
    }

    @Override
    public void refresh() {
        new SqlQuery(ActInvoice.this, spQuery, "{call " + Global.database.getPrefix() + "getinvoice(?,?)}", new String[]{"0", String.valueOf(Global.wh_Grp_Id)});
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
        if (tag == spQuery) {
            invoices.clear();
            while (rs != null && rs.next()) {
                Invoice i = new Invoice(rs.getInt("id"), rs.getString("no"), rs.getString("invoice_date"), rs.getString("cus_name"),
                        rs.getInt("qty"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getString("usr_name"),
                        OrderPay.valueOf(rs.getString("pay")), rs.getBoolean("ship"), rs.getString("remark"), rs.getInt("order_id"), rs.getString("order_no"),
                        rs.getFloat("paid"), rs.getFloat("charge"), rs.getFloat("refund"), OrderStat.valueOf(rs.getString("stat")));
                invoices.add(i);
            }
            if (adap != null) adap.notifyDataSetChanged();
        }
    }
}
