package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Invoice;
import com.newit.bsrpos_sql.Model.InvoiceItem;
import com.newit.bsrpos_sql.Model.OrderPay;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.AdpPrint;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActInvoicePrint extends ActBase {

    private Invoice invoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.invoiceprint);

        hideActionBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        } else invoice = (Invoice) bundle.getSerializable("invoice");

        TextView invoiceprint_no = (TextView) findViewById(R.id.invoiceprint_no);
        TextView invoiceprint_date = (TextView) findViewById(R.id.invoiceprint_date);
        TextView invoiceprint_sum = (TextView) findViewById(R.id.invoiceprint_sum);
        CheckBox invoiceprint_ship = (CheckBox) findViewById(R.id.invoiceprint_ship);
        CheckBox invoiceprint_cash = (CheckBox) findViewById(R.id.invoiceprint_cash);
        CheckBox invoiceprint_transfer = (CheckBox) findViewById(R.id.invoiceprint_transfer);
        CheckBox invoiceprint_credit = (CheckBox) findViewById(R.id.invoiceprint_credit);
        TextView invoiceprint_sales = (TextView) findViewById(R.id.invoiceprint_sales);

        invoiceprint_no.setText("เลขที่ " + invoice.getNo());
        invoiceprint_date.setText("วันที่ " + invoice.getDate());
        invoiceprint_sum.setText("รวม " + String.valueOf(invoice.getAmount()));
        invoiceprint_ship.setChecked(invoice.isShip());
        invoiceprint_cash.setChecked(invoice.getPay() == OrderPay.Cash);
        invoiceprint_transfer.setChecked(invoice.getPay() == OrderPay.Transfer);
        invoiceprint_credit.setChecked(invoice.getPay() == OrderPay.Credit);
        invoiceprint_sales.setText("ฝ่ายขาย " + invoice.getUsr_name());
        AdpCustom<InvoiceItem> adapOrderItem = new AdpCustom<InvoiceItem>(R.layout.listing_grid_invoiceprint, getLayoutInflater(), invoice.getItems()) {
            @Override
            protected void populateView(View v, InvoiceItem model) {
                TextView invoiceprint_no = (TextView) v.findViewById(R.id.invoiceprint_no);
                TextView invoiceprint_desc = (TextView) v.findViewById(R.id.invoiceprint_desc);
                TextView invoiceprint_qty = (TextView) v.findViewById(R.id.invoiceprint_qty);
                invoiceprint_no.setText(String.valueOf(model.getNo()));
                invoiceprint_desc.setText(model.getProd_name());
                invoiceprint_qty.setText(String.valueOf(model.getAmount()));
            }
        };
        ListView list = (ListView) findViewById(R.id.invoiceprint_list);
        list.setAdapter(adapOrderItem);
        AdpPrint.formatListView(list);

        printPDF(invoice.getNo(), R.id.relativeLayout_ActInvoicePrint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException {
    }
}
