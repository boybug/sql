package com.newit.bsrpos_sql.Activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderPay;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.Model.RecordStat;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActOrderInputPayment extends ActBase {

    private Order order;

    private TextView orderiteminputpayment_no, orderiteminputpayment_qty, orderiteminputpayment_wgt,
            orderiteminputpayment_amt, orderinput_cus, orderiteminputpayment_remark;
    private RadioButton radio_paycash, radio_paytranfer, radio_paycredit;
    private Switch switch_payship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderinput_payment);

        orderinput_cus = (TextView) findViewById(R.id.orderinput_cus);
        orderiteminputpayment_no = (TextView) findViewById(R.id.orderiteminputpayment_no);
        orderiteminputpayment_qty = (TextView) findViewById(R.id.orderiteminputpayment_qty);
        orderiteminputpayment_wgt = (TextView) findViewById(R.id.orderiteminputpayment_wgt);
        orderiteminputpayment_amt = (TextView) findViewById(R.id.orderiteminputpayment_amt);
        orderiteminputpayment_remark = (TextView) findViewById(R.id.orderiteminputpayment_remark);

        switch_payship = (Switch) findViewById(R.id.switch_payship);
        RadioGroup radio_group = (RadioGroup) findViewById(R.id.radio_group);
        radio_paycash = (RadioButton) findViewById(R.id.radio_paycash);
        radio_paytranfer = (RadioButton) findViewById(R.id.radio_paytranfer);
        radio_paycredit = (RadioButton) findViewById(R.id.radio_paycredit);


        Bundle bundle = getIntent().getExtras();
        order = (Order) bundle.getSerializable("order");

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
                order.setShip(isChecked ? true : false);
            }
        });

        redrawOrder();

        //region SAVE
        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);
        bt_cmd_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getStat() == OrderStat.Confirm)
                    Toast.makeText(ActOrderInputPayment.this, "เอกสารได้ถูกยืนยันไปแล้ว.", Toast.LENGTH_SHORT).show();
                else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ActOrderInputPayment.this);
                    dialog.setTitle("ยืนยันเอกสาร");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(true);
                    dialog.setMessage("เอกสารยันแล้วจะไม่สามารถแก้ไขได้   คุณต้องการยืนยันใช่หรือไม่");
                    dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SqlResult result = save();
                            if (result.getMsg() == null) backPressed(ActOrder.class);
                            else MessageBox(result.getMsg());
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
        order.setPay(OrderPay.Cash);
        orderinput_cus.setText(order.getCus_name());
        orderiteminputpayment_no.setText(order.getNo());
        orderiteminputpayment_qty.setText(String.valueOf(order.getQty()));
        orderiteminputpayment_wgt.setText(String.valueOf(order.getWeight()));
        orderiteminputpayment_amt.setText(String.valueOf(order.getAmount()));
        if (order.isShip()) switch_payship.setChecked(true);
        else switch_payship.setChecked(false);

        if (order.getStat() == OrderStat.Confirm) {
            if (order.getPay() == OrderPay.Cash) {
                radio_paycash.setChecked(true);
            } else if (order.getPay() == OrderPay.Transfer) {
                radio_paytranfer.setChecked(true);
            } else if (order.getPay() == OrderPay.Credit) {
                radio_paycredit.setChecked(true);
            }
            orderiteminputpayment_remark.setText(order.getRemark());
        }
    }

    public SqlResult save() {
        SqlResult result = new SqlResult();
        try {
            String[] params = {String.valueOf(order.getId()), order.isShip() ? "1" : "0", String.valueOf(order.getPay()), String.valueOf(orderiteminputpayment_remark.getText().toString())};
            ResultSet rs = SqlServer.execute("{call POS.dbo.setorderpay(?,?,?,?)}", params);
            if (rs != null && rs.next()) {
                result.setIden(rs.getInt("Iden"));
                result.setMsg(rs.getString("Msg"));
                if (result.getIden() > 0) {
                    order.setRecordStat(RecordStat.NULL);
                } else result.setMsg("ไม่ได้รับคำตอบจาก server");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.setMsg("ไม่สามารถเชื่อมต่อกับ server");
        }
        return result;
    }


}