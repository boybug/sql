package com.newit.bsrpos_sql.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderPay;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActOrderInputPayment extends ActBase {

    private Order order;
    private final int spUpdate = 1;

    private TextView orderiteminputpayment_no, orderiteminputpayment_qty, orderiteminputpayment_wgt,
            orderiteminputpayment_amt, orderinput_cus, orderiteminputpayment_remark;
    private RadioButton radio_paycash, radio_paytranfer, radio_paycredit;
    private Switch switch_payship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderinput_payment);

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
        bt_cmd_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getStat() == OrderStat.Confirm) MessageBox("เอกสารได้ถูกยืนยันไปแล้ว.");
                else if (order.getItems().size() == 0) MessageBox("ไม่มีรายการสินค้า");
                else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ActOrderInputPayment.this);
                    dialog.setTitle("ยืนยันเอกสาร");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(true);
                    dialog.setMessage("เอกสารยันแล้วจะไม่สามารถแก้ไขได้   คุณต้องการยืนยันใช่หรือไม่");
                    dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            order.setRemark(orderiteminputpayment_remark.getText().toString());
                            String[] params = {String.valueOf(order.getId()), order.isShip() ? "1" : "0", String.valueOf(order.getPay()), String.valueOf(order.getRemark())};
                            new SqlQuery(ActOrderInputPayment.this, spUpdate, "{call POS.dbo.setorderpay(?,?,?,?)}", params);
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
        getMenuInflater().inflate(R.menu.base, menu);
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
        if (tag == spUpdate) {
            if (rs != null && rs.next()) {
                SqlResult result = new SqlResult(rs);
                if (result.getMsg() == null) {
                    Intent intent = new Intent();
                    intent.putExtra("FINISH", true);
                    setResult(2, intent);
                    backPressed(ActOrder.class);
                } else MessageBox(result.getMsg());
            }
        }
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        Intent intent = new Intent(ActOrderInputPayment.this, ActOrderInput.class);
        intent.putExtras(bundle);
        ActOrderInputPayment.this.startActivity(intent);
    }
}