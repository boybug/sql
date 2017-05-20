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

    private Invoice order;

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
        } else order = (Invoice) bundle.getSerializable("invoice");

        TextView textno = (TextView) findViewById(R.id.textno);
        TextView textdate = (TextView) findViewById(R.id.textdate);
        TextView textsum = (TextView) findViewById(R.id.textsum);
        CheckBox checkship = (CheckBox) findViewById(R.id.checkship);
        CheckBox checkcash = (CheckBox) findViewById(R.id.checkcash);
        CheckBox checktransfer = (CheckBox) findViewById(R.id.checktransfer);
        CheckBox checkcredit = (CheckBox) findViewById(R.id.checkcredit);

        textno.setText("เลขที่ " + order.getNo());
        textdate.setText("วันที่ " + order.getDate());
        textsum.setText("รวม " + String.valueOf(order.getAmount()));
        checkship.setChecked(order.isShip());
        checkcash.setChecked(order.getPay() == OrderPay.Cash);
        checktransfer.setChecked(order.getPay() == OrderPay.Transfer);
        checkcredit.setChecked(order.getPay() == OrderPay.Credit);

        AdpCustom<InvoiceItem> adapOrderItem = new AdpCustom<InvoiceItem>(R.layout.listing_grid_invoice_print, getLayoutInflater(), order.getItems()) {
            @Override
            protected void populateView(View v, InvoiceItem model) {
                TextView orderitem_no = (TextView) v.findViewById(R.id.invoiceitem_no);
                TextView orderitem_desc = (TextView) v.findViewById(R.id.invoiceitem_desc);
                TextView orderitem_qty = (TextView) v.findViewById(R.id.invoiceitem_qty);
                orderitem_no.setText(String.valueOf(model.getNo()));
                orderitem_desc.setText(model.getProd_name());
                orderitem_qty.setText(String.valueOf(model.getAmount()));
            }
        };
        ListView mylist = (ListView) findViewById(R.id.mylist);
        mylist.setAdapter(adapOrderItem);
        HelperList.getListViewSize(mylist);

        printPDF();

    }

    public void printPDF() {

        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print("POS Invoice " + order.getNo(), new AdpPrint(ActInvoicePrint.this, findViewById(R.id.relativeLayout)), null);
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
