package com.newit.bsrpos_sql.Activity;


import android.app.Activity;
import android.os.Bundle;
import android.print.PrintManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.Model.OrderPay;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;

public class ActOrderPrint extends Activity {

    private Order order;
    private AdpCustom<OrderItem> adapOrderItem;
    ViewPrintAdapter adep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.order_print);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
        } else order = (Order) bundle.getSerializable("order");

        TextView textno = (TextView) findViewById(R.id.textno);
        TextView textdate = (TextView) findViewById(R.id.textdate);
        CheckBox checkpos = (CheckBox) findViewById(R.id.checkpos);
        CheckBox checkship = (CheckBox) findViewById(R.id.checkship);
        CheckBox checkcash = (CheckBox) findViewById(R.id.checkcash);
        CheckBox checktrans = (CheckBox) findViewById(R.id.checktrans);
        CheckBox checkcredit = (CheckBox) findViewById(R.id.checkcredit);
        TextView textsum = (TextView) findViewById(R.id.textsum);

        textno.setText("เลขที่ " + order.getNo());
        textdate.setText("วันที่ " + order.getDate());
        textsum.setText("รวม " + String.valueOf(order.getAmount()));
        checkpos.setChecked(true);
        checkship.setChecked(order.isShip());
        checkcash.setChecked(order.getPay() == OrderPay.Cash);
        checktrans.setChecked(order.getPay() == OrderPay.Transfer);
        checkcredit.setChecked(order.getPay() == OrderPay.Credit);

        adapOrderItem = new AdpCustom<OrderItem>(R.layout.listing_grid_orderitem_print, getLayoutInflater(), order.getItems()) {
            @Override
            protected void populateView(View v, OrderItem model) {
                TextView orderitem_no = (TextView) v.findViewById(R.id.orderitem_no);
                TextView orderitem_desc = (TextView) v.findViewById(R.id.orderitem_desc);
                TextView orderitem_qty = (TextView) v.findViewById(R.id.orderitem_qty);
                orderitem_no.setText(String.valueOf(model.getNo()));
                orderitem_desc.setText(model.getProduct().getName());
                orderitem_qty.setText(String.valueOf(model.getPrice() * model.getQty()));
            }
        };
        ListView mylist = (ListView) findViewById(R.id.mylist);
        mylist.setAdapter(adapOrderItem);
        HelperList.getListViewSize(mylist);

        adep = new ViewPrintAdapter(ActOrderPrint.this, findViewById(R.id.relativeLayoutprint));
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        printManager.print("ใบแจ้งหนี้ขาย", adep, null);

    }

}

