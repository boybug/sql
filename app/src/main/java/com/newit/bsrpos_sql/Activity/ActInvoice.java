package com.newit.bsrpos_sql.Activity;

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

        setTitle("รายการบิลขาย@" + Global.wh_grp_name);
        setSwipeRefresh(R.id.swipe_refresh, R.id.listing_list);

        adap = new AdpCustom<Invoice>(R.layout.listing_grid_invoice, getLayoutInflater(), invoices) {
            @Override
            protected void populateView(View v, Invoice invoice) {

                TextView invoice_no = (TextView) v.findViewById(R.id.invoice_no);
                invoice_no.setText(invoice.getNo());

                TextView invoice_cus = (TextView) v.findViewById(R.id.invoice_cus);
                invoice_cus.setText(invoice.getCus_name());

                TextView invoice_amount = (TextView) v.findViewById(R.id.invoice_amount);
                invoice_amount.setText(String.valueOf(invoice.getAmount()));

                TextView invoice_ship = (TextView) v.findViewById(R.id.invoice_ship);
                invoice_ship.setText(String.valueOf(invoice.isShip() ? "ส่ง" : "ไม่ส่ง"));

                TextView invoice_usr = (TextView) v.findViewById(R.id.invoice_usr);
                invoice_usr.setText("ผู้เปิดบิล : " + String.valueOf(invoice.getUsr_name()));

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
                //todo: ทำหน้าโชว์ invoice item
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("invoice", adap.getModels().get(position));
//                Intent intent = new Intent(ActInvoice.this, ActInvoiceInput.class);
//                intent.putExtras(bundle);
//                ActInvoice.this.startActivity(intent);
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
        getMenuInflater().inflate(R.menu.contextmenu, menu);
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
            invoices.clear();
            while (rs != null && rs.next()) {
                Invoice i = new Invoice(rs.getInt("id"), rs.getString("no"), rs.getString("invoice_date"), rs.getString("cus_name"),
                        rs.getInt("qty"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getString("usr_name"),
                        OrderPay.valueOf(rs.getString("pay")), rs.getBoolean("ship"), rs.getString("remark"), rs.getString("order_no"));
                invoices.add(i);
            }
            if (adap != null) adap.notifyDataSetChanged();
        }
    }
}