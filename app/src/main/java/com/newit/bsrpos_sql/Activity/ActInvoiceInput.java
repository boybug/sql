package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Invoice;
import com.newit.bsrpos_sql.Model.InvoiceItem;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActInvoiceInput extends ActBase {

    private Invoice invoice;
    private AdpCustom<InvoiceItem> adap;

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
        setTitle(invoice.getNo() + "@" + Global.wh_grp_name);

        TextView invoiceinput_cus = (TextView) findViewById(R.id.invoiceinput_cus);
        TextView invoiceinput_no = (TextView) findViewById(R.id.invoiceinput_no);
        TextView invoiceinput_qty = (TextView) findViewById(R.id.invoiceinput_qty);
        TextView invoiceinput_wgt = (TextView) findViewById(R.id.invoiceinput_wgt);
        TextView invoiceinput_amt = (TextView) findViewById(R.id.invoiceinput_amt);
        TextView invoiceinput_listtitle = (TextView) findViewById(R.id.invoiceinput_listtitle);
        TextView invoiceinput_order = (TextView) findViewById(R.id.invoiceinput_order);
        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);
        bt_cmd_save.setVisibility(View.GONE);

        invoiceinput_cus.setText(String.valueOf(invoice.getCus_name()));
        invoiceinput_no.setText(invoice.getNo());
        invoiceinput_qty.setText(String.valueOf(invoice.getQty()));
        invoiceinput_wgt.setText(String.valueOf(invoice.getWeight()));
        invoiceinput_amt.setText(String.valueOf(invoice.getAmount()));
        invoiceinput_listtitle.setText("รายการสินค้า(" + String.valueOf(invoice.getItems().size()) + ")");
        invoiceinput_order.setText(invoice.getOrder_no());

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
                //todo : click แล้วไปหน้า order
            }
        });
    }

    @Override
    public void processFinish(ResultSet rs, int tag) throws SQLException {
    }
}
