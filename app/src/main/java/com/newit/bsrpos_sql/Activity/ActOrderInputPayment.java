package com.newit.bsrpos_sql.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Invoice;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderPay;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActOrderInputPayment extends ActBase {

    private Order order;
    private final int spUpdate = 1;
    private final int spGetInvoice = 2;
    private List<Invoice> invoices = new ArrayList<>();

    private TextView orderiteminputpayment_no, orderiteminputpayment_qty, orderiteminputpayment_wgt, orderiteminputpayment_amt, orderinput_cus, orderiteminputpayment_remark;
    private RadioButton radio_paycash, radio_paytranfer, radio_paycredit;
    private Switch switch_payship;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderinput_payment);

        setTitle("ยืนยันใบสั่งขาย@ " + Global.wh_grp_name);

        orderinput_cus = (TextView) findViewById(R.id.orderinput_cus);
        orderiteminputpayment_no = (TextView) findViewById(R.id.orderinput_no);
        orderiteminputpayment_qty = (TextView) findViewById(R.id.orderinput_qty);
        orderiteminputpayment_wgt = (TextView) findViewById(R.id.orderinput_wgt);
        orderiteminputpayment_amt = (TextView) findViewById(R.id.orderinput_amt);
        orderiteminputpayment_remark = (TextView) findViewById(R.id.orderiteminputpayment_remark);

        switch_payship = (Switch) findViewById(R.id.switch_payship);
        RadioGroup radio_group = (RadioGroup) findViewById(R.id.radio_group);
        radio_paycash = (RadioButton) findViewById(R.id.radio_paycash);
        radio_paytranfer = (RadioButton) findViewById(R.id.radio_paytranfer);
        radio_paycredit = (RadioButton) findViewById(R.id.radio_paycredit);


        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            MessageBox("error");
            finish();
        } else order = (Order) bundle.getSerializable("order");

        if (order.getStat() == OrderStat.Confirm)
            getInvoices();

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radio_paycash:
                        order.setPay(OrderPay.Cash);
                        break;
                    case R.id.radio_paytranfer:
                        order.setPay(OrderPay.Transfer);
                        break;
                    case R.id.radio_paycredit:
                        order.setPay(OrderPay.Credit);
                        break;
                }
            }
        });
        switch_payship.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                order.setShip(isChecked);
            }
        });

        redrawOrder();

        //region SAVE
        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);
        bt_cmd_save.setText("ยืนยันใบสั่งขาย");
        bt_cmd_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getUsr_id() != Global.user.getId())
                    MessageBox("ไม่สามารถยืนยันใบสั่งของคนอื่น");
                else if (order.getStat() == OrderStat.Confirm)
                    MessageBox("ใบสั่งขายได้ถูกยืนยันไปแล้ว");
                else if (order.getItems().size() == 0)
                    MessageBox("ไม่มีรายการสินค้า");
                else {
                    AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new AlertDialog.Builder(ActOrderInputPayment.this, android.R.style.Theme_Material_Light_Dialog_Alert) : new AlertDialog.Builder(ActOrderInputPayment.this);
                    dialog.setTitle("ยืนยันใบสั่งขาย");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(true);
                    dialog.setMessage("ใบสั่งยืนยันแล้วจะไม่สามารถแก้ไขได้ คุณต้องการยืนยันใช่หรือไม่");
                    dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            order.setRemark(orderiteminputpayment_remark.getText().toString());
                            String[] params = {String.valueOf(order.getId()), order.isShip() ? "1" : "0", String.valueOf(order.getPay()), String.valueOf(order.getRemark())};
                            new SqlQuery(ActOrderInputPayment.this, spUpdate, "{call " + Global.database.getPrefix() + "setorderpay(?,?,?,?)}", params);
                        }
                    });
                    dialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            }
        });

        //endregion
    }

    private void getInvoices() {
        invoices.clear();
        new SqlQuery(ActOrderInputPayment.this, spGetInvoice, "{call " + Global.database.getPrefix() + "getinvoice(?,?)}", new String[]{String.valueOf(order.getId()), String.valueOf(order.getWh_grp_Id())});
    }

    private void redrawOrder() {
        orderinput_cus.setText(order.getCus_name());
        orderiteminputpayment_no.setText(order.getNo());
        orderiteminputpayment_qty.setText(String.valueOf(order.getQty()));
        orderiteminputpayment_wgt.setText(String.valueOf(order.getWeight()));
        orderiteminputpayment_amt.setText(String.valueOf(order.getAmount()));
        switch_payship.setChecked(order.isShip());

        if (order.getStat() == OrderStat.Confirm) {
            if (order.getPay() == OrderPay.Cash) {
                radio_paycash.setChecked(true);
            } else if (order.getPay() == OrderPay.Transfer) {
                radio_paytranfer.setChecked(true);
            } else if (order.getPay() == OrderPay.Credit) {
                radio_paycredit.setChecked(true);
            }
            orderiteminputpayment_remark.setText(order.getRemark());
        } else order.setPay(OrderPay.Cash);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nologout, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("invoice", invoices.get(item.getItemId()));
        Intent intent = new Intent(ActOrderInputPayment.this, ActInvoicePrint.class);
        intent.putExtras(bundle);
        ActOrderInputPayment.this.startActivity(intent);
        return true;
    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException {
        if (tag == spUpdate) {
            if (rs != null && rs.next()) {
                SqlResult result = new SqlResult(rs);
                if (result.getMsg() == null) {
                    MessageBox("ยืนยันใบสั่งขายสำเร็จ");
                    getInvoices();
                    //todo: ยืนยันแล้ว ลง 21101 แล้ว จะเตะ firebase + pos ยังไง
                } else MessageBox(result.getMsg());
            }
        } else if (tag == spGetInvoice) {
            while (rs != null && rs.next()) {
                Invoice i = new Invoice(rs.getInt("id"), rs.getString("no"), rs.getString("invoice_date"), rs.getString("cus_name"),
                        rs.getInt("qty"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getString("usr_name"),
                        OrderPay.valueOf(rs.getString("pay")), rs.getBoolean("ship"), rs.getString("remark"), rs.getInt("order_id"), rs.getString("order_no"));
                invoices.add(i);
            }
            if (menu != null) {
                menu.clear();
                for (int i = 0; i < invoices.size(); i++) {
                    menu.add(0, i, Menu.NONE, "พิมพ์ใบเสร็จ: " + invoices.get(i).getNo());
                }
            }
        }
    }

}