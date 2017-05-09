package com.newit.bsrpos_sql.Activity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.R;

public class ActOrderInputPayment extends ActBase {

    private Order order;

    private TextView orderiteminputpayment_no, orderiteminputpayment_qty, orderiteminputpayment_wgt, orderiteminputpayment_amt, orderinput_cus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderinput_payment);

        orderinput_cus = (TextView) findViewById(R.id.orderinput_cus);
        orderiteminputpayment_no = (TextView) findViewById(R.id.orderiteminputpayment_no);
        orderiteminputpayment_qty = (TextView) findViewById(R.id.orderiteminputpayment_qty);
        orderiteminputpayment_wgt = (TextView) findViewById(R.id.orderiteminputpayment_wgt);
        orderiteminputpayment_amt = (TextView) findViewById(R.id.orderiteminputpayment_amt);


        Switch switch_payship = (Switch) findViewById(R.id.switch_payship);
        RadioGroup radio_group = (RadioGroup) findViewById(R.id.radio_group);
        RadioButton radio_paycash = (RadioButton) findViewById(R.id.radio_paycash);
        RadioButton radio_paytranfer = (RadioButton) findViewById(R.id.radio_paytranfer);
        RadioButton radio_paycredit = (RadioButton) findViewById(R.id.radio_paycredit);

        Bundle bundle = getIntent().getExtras();
        order = (Order) bundle.getSerializable("order");
        redrawOrder();
        //region SAVE
//        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);

        //endregion

    }

    private void redrawOrder() {
        orderinput_cus.setText(String.valueOf(order.getCus_name()));
        orderiteminputpayment_no.setText(order.getNo());
        orderiteminputpayment_qty.setText(String.valueOf(order.getQty()));
        orderiteminputpayment_wgt.setText(String.valueOf(order.getWeight()));
        orderiteminputpayment_amt.setText(String.valueOf(order.getAmount()));
    }


}