package com.newit.bsrpos_sql.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Invoice;
import com.newit.bsrpos_sql.Model.InvoiceItem;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;

import java.io.FileOutputStream;
import java.io.IOException;

public class ActInvoicePrint extends Activity {

    private Invoice order;
    private AdpCustom<InvoiceItem> adapOrderItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.invoice_print);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        } else order = (Invoice) bundle.getSerializable("invoice");

        adapOrderItem = new AdpCustom<InvoiceItem>(R.layout.listing_grid_invoice_print, getLayoutInflater(), order.getItems()) {
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
        printManager.print("POS Invoice " + order.getNo(), new ViewPrintAdapter(this,
                findViewById(R.id.relativeLayout)), null);
    }




}
