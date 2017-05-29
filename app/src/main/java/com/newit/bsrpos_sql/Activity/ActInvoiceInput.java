package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Invoice;
import com.newit.bsrpos_sql.Model.InvoiceItem;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderPay;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActInvoiceInput extends ActBase {

    private Invoice invoice;
    private AdpCustom<InvoiceItem> adap;
    private final int spQueryOrder = 1;
    private final int spQueryInvoiceItem = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoiceinput);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            MessageBox("error");
            finish();
        }
        invoice = (Invoice) (bundle != null ? bundle.getSerializable("invoice") : null);
        new SqlQuery(ActInvoiceInput.this, spQueryInvoiceItem, "{call " + Global.database.getPrefix() + "getinvoiceitem(?)}", new String[]{String.valueOf(invoice.getId())});
        setTitle("ใบแจ้งหนี้ " + invoice.getNo() + "@" + Global.wh_grp_name);

        TextView invoiceinput_cus = (TextView) findViewById(R.id.invoiceinput_cus);
        TextView invoiceinput_no = (TextView) findViewById(R.id.invoiceinput_no);
        TextView invoiceinput_qty = (TextView) findViewById(R.id.invoiceinput_qty);
        TextView invoiceinput_wgt = (TextView) findViewById(R.id.invoiceinput_wgt);
        TextView invoiceinput_amt = (TextView) findViewById(R.id.invoiceinput_amt);
        TextView invoiceinput_listtitle = (TextView) findViewById(R.id.invoiceinput_listtitle);
        TextView invoiceinput_order = (TextView) findViewById(R.id.invoiceinput_order);
        TextView invoiceinput_user = (TextView) findViewById(R.id.invoiceinput_user);
        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);
        bt_cmd_save.setVisibility(View.GONE);

        invoiceinput_cus.setText(String.valueOf(invoice.getCus_name()));
        invoiceinput_no.setText(invoice.getNo());
        invoiceinput_qty.setText(String.valueOf(invoice.getQty()));
        invoiceinput_wgt.setText(String.valueOf(invoice.getWeight()));
        invoiceinput_amt.setText(String.valueOf(invoice.getAmount()));
        invoiceinput_listtitle.setText("รายการสินค้า(" + String.valueOf(invoice.getItems().size()) + ")");
        invoiceinput_order.setText(invoice.getOrder_no());
        invoiceinput_user.setText(invoice.getUsr_name());

        adap = new AdpCustom<InvoiceItem>(R.layout.listing_grid_invoiceitem, getLayoutInflater(), invoice.getItems()) {
            @Override
            protected void populateView(View v, InvoiceItem model) {
                TextView invoiceitem_no = (TextView) v.findViewById(R.id.invoiceitem_no);
                TextView invoiceitem_desc = (TextView) v.findViewById(R.id.invoiceitem_desc);
                TextView invoiceitem_qty = (TextView) v.findViewById(R.id.invoiceitem_qty);
                invoiceitem_no.setText(String.valueOf(model.getNo()));
                invoiceitem_desc.setText(model.getProd_name());
                invoiceitem_qty.setText(String.valueOf(String.valueOf(model.getPrice()) + "x" + model.getQty()));
            }
        };
        final ListView list = (ListView) findViewById(R.id.list_order_item);
        list.setAdapter(adap);

        invoiceinput_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SqlQuery(ActInvoiceInput.this, spQueryOrder, "{call " + Global.database.getPrefix() + "getorder(?,?)}", new String[]{String.valueOf(Global.wh_Grp_Id), String.valueOf(invoice.getOrder_Id())});
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nologout, menu);
        return true;
    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException {
        if (tag == spQueryInvoiceItem) {
            while (rs != null && rs.next()) {
                InvoiceItem item = new InvoiceItem(rs.getInt("no"), rs.getString("prod_name"), rs.getInt("qty"), rs.getFloat("price"), rs.getFloat("weight"), rs.getFloat("amount"));
                invoice.getItems().add(item);
            }

        } else if (tag == spQueryOrder) {
            if (rs != null && rs.next()) {
                Order o = new Order(rs.getInt("id"), rs.getString("no"), rs.getString("order_date"),
                        rs.getInt("cus_id"), rs.getString("cus_name"), rs.getInt("wh_grp_id"), OrderStat.valueOf(rs.getString("order_stat")),
                        rs.getInt("qty"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getInt("usr_id"), rs.getString("usr_name"),
                        OrderPay.valueOf(rs.getString("pay")), rs.getBoolean("ship"), rs.getString("remark"),rs.getFloat("paid"),rs.getFloat("charge"),rs.getFloat("refund"));
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", o);
                Intent intent = new Intent(ActInvoiceInput.this, ActOrderInput.class);
                intent.putExtras(bundle);
                ActInvoiceInput.this.startActivityForResult(intent, 4);
            }
        }
    }
}
