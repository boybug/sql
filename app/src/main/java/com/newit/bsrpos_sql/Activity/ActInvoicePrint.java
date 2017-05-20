package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.print.PrintManager;
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
import com.newit.bsrpos_sql.Util.HelperList;

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
        setContentView(R.layout.invoice_print);

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

        invoiceprint_no.setText("เลขที่ " + invoice.getNo());
        invoiceprint_date.setText("วันที่ " + invoice.getDate());
        invoiceprint_sum.setText("รวม " + String.valueOf(invoice.getAmount()));
        invoiceprint_ship.setChecked(invoice.isShip());
        invoiceprint_cash.setChecked(invoice.getPay() == OrderPay.Cash);
        invoiceprint_transfer.setChecked(invoice.getPay() == OrderPay.Transfer);
        invoiceprint_credit.setChecked(invoice.getPay() == OrderPay.Credit);

        AdpCustom<InvoiceItem> adapOrderItem = new AdpCustom<InvoiceItem>(R.layout.listing_grid_invoice_print, getLayoutInflater(), invoice.getItems()) {
            @Override
            protected void populateView(View v, InvoiceItem model) {
                TextView invoiceitem_no = (TextView) v.findViewById(R.id.invoiceitem_no);
                TextView invoiceitem_desc = (TextView) v.findViewById(R.id.invoiceitem_desc);
                TextView invoiceitem_qty = (TextView) v.findViewById(R.id.invoiceitem_qty);
                invoiceitem_no.setText(String.valueOf(model.getNo()));
                invoiceitem_desc.setText(model.getProd_name());
                invoiceitem_qty.setText(String.valueOf(model.getAmount()));
            }
        };
        ListView mylist = (ListView) findViewById(R.id.mylist);
        mylist.setAdapter(adapOrderItem);
        HelperList.getListViewSize(mylist);

        printPDF();

    }

    public void printPDF() {

        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print("POS Invoice " + invoice.getNo(), new AdpPrint(ActInvoicePrint.this, findViewById(R.id.relativeLayout)), null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    public void processFinish(ResultSet rs, int tag) throws SQLException {
    }
}
