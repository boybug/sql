package com.newit.bsrpos_sql.Activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.print.PrintHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.AdpPrint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActOrderPrint extends ActBase {

    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderprint);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        } else order = (Order) bundle.getSerializable("order");

        TextView orderprint_no = (TextView) findViewById(R.id.orderprint_no);
        TextView orderprint_date = (TextView) findViewById(R.id.orderprint_date);
        TextView orderprint_cus = (TextView) findViewById(R.id.orderprint_cus);
        TextView orderprint_sales = (TextView) findViewById(R.id.orderprint_sales);

        orderprint_no.setText("เลขที่ " + order.getNo());
        orderprint_date.setText("วันที่ " + order.getDate());
        orderprint_cus.setText("ลูกค้า " + order.getCus_name());
        orderprint_sales.setText("ฝ่ายขาย " + order.getUsr_name());

        AdpCustom<OrderItem> adap = new AdpCustom<OrderItem>(R.layout.listing_grid_orderprint, getLayoutInflater(), order.getItems()) {
            @Override
            protected void populateView(View v, OrderItem model) {
                TextView orderprint_no = (TextView) v.findViewById(R.id.orderprint_no);
                TextView orderprint_desc = (TextView) v.findViewById(R.id.orderprint_desc);
                TextView orderprint_orderqty = (TextView) v.findViewById(R.id.orderprint_orderqty);
                orderprint_no.setText(String.valueOf(model.getNo()));
                orderprint_desc.setText(model.getProduct().getName());
                orderprint_orderqty.setText(String.valueOf(model.getQty()));
            }
        };
        ListView list = (ListView) findViewById(R.id.orderprint_list);
        list.setAdapter(adap);
        AdpPrint.formatListView(list);
        //printPDF(order.getNo(), R.id.relativeLayout_ActOrderPrint);
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File("/sdcard/screenshotdemo.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            MessageBox(e.getMessage());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nologout, menu);
        menu.add(0, 1, Menu.NONE, "พิมพ์ใบหยิบสินค้า");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            TableLayout orderprint_table = (TableLayout) findViewById(R.id.orderprint_table);
            Bitmap bitmap1 = loadBitmapFromView(orderprint_table, orderprint_table.getWidth(), orderprint_table.getHeight());
            PrintHelper photoPrinter = new PrintHelper(this);
            photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            photoPrinter.printBitmap("droids.jpg - test print", bitmap1);
        }
        return true;
    }


}
