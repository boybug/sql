package com.newit.bsrpos_sql.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    private List<Invoice> invoices = new ArrayList<>();

    private TextView orderiteminputpayment_no, orderiteminputpayment_qty, orderiteminputpayment_wgt, orderiteminputpayment_amt, orderinput_cus, orderiteminputpayment_remark, orderiteminputpayment_bank;
    private RadioButton radio_paycash, radio_paytranfer, radio_paycredit;
    private Switch switch_payship;
    private EditText orderiteminputpayment_paid, orderiteminputpayment_charge, orderiteminputpayment_refund;
    private Menu menu;
    private WebView webView;
    private final int spUpdate = 1;
    private final int spGetInvoice = 2;
    private final int spQueryInvoicePrint = 3;
    private boolean stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderinput_payment);

        setTitle("ยืนยันใบสั่งขาย@ " + Global.getwh_grp_name(getApplicationContext()));

        orderinput_cus = (TextView) findViewById(R.id.orderinput_cus);
        orderiteminputpayment_no = (TextView) findViewById(R.id.orderinput_no);
        orderiteminputpayment_qty = (TextView) findViewById(R.id.orderinput_qty);
        orderiteminputpayment_wgt = (TextView) findViewById(R.id.orderinput_wgt);
        orderiteminputpayment_amt = (TextView) findViewById(R.id.orderinput_amt);
        orderiteminputpayment_remark = (TextView) findViewById(R.id.orderiteminputpayment_remark);
        orderiteminputpayment_charge = (EditText) findViewById(R.id.orderiteminputpayment_charge);
        orderiteminputpayment_paid = (EditText) findViewById(R.id.orderiteminputpayment_paid);
        orderiteminputpayment_refund = (EditText) findViewById(R.id.orderiteminputpayment_refund);
        orderiteminputpayment_bank = (TextView) findViewById(R.id.orderiteminputpayment_bank);

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
                            order.setBank_id(-1);
                            order.setBank_name("");
                            orderiteminputpayment_bank.setText("");
                            stop = true;
                            calRefund();
                            break;
                        case R.id.radio_paytranfer:
                            order.setPay(OrderPay.Transfer);
                            stop = true;
                            calRefund();
                            if (order.getStat() != OrderStat.Confirm) {
                                Intent intent = new Intent(ActOrderInputPayment.this, ActBank.class);
                                startActivityForResult(intent, 1);
                            }
                            break;
                        case R.id.radio_paycredit:
                            order.setPay(OrderPay.Credit);
                            order.setBank_id(-1);
                            order.setBank_name("");
                            orderiteminputpayment_bank.setText("");
                            stop = true;
                            calRefund();
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

        orderiteminputpayment_paid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!stop) {
                    String str = s.toString().replace(",", "");
                    float charge;
                    if (order.getPay() == OrderPay.Credit) {
                        charge = (float) ((order.getAmount() * 2) / 100.00);
                    } else charge = 0;
                    if (str.length() == 0) {
                        str = "0";
                    }
                    float paid = Float.parseFloat(str);
                    float refund = paid - order.getAmount() - charge;
                    orderiteminputpayment_refund.setText(Global.formatMoney(refund));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (orderiteminputpayment_paid.length() == 0) {
                    orderiteminputpayment_paid.setText(Global.formatMoney(0));
                    orderiteminputpayment_paid.setSelectAllOnFocus(true);
                }
            }
        });

        orderiteminputpayment_refund.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (orderiteminputpayment_refund.length() == 0) {
                    orderiteminputpayment_refund.setText(Global.formatMoney(0));
                    orderiteminputpayment_refund.setSelectAllOnFocus(true);
                }
            }
        });

        //region SAVE
        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);
        bt_cmd_save.setText("ยืนยันใบสั่งขาย");
        bt_cmd_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getUsr_id() != Global.getUser(getApplicationContext()).getId())
                    MessageBox("ไม่สามารถยืนยันใบสั่งของคนอื่น");
                else if (order.getStat() == OrderStat.Confirm)
                    MessageBox("ใบสั่งขายได้ถูกยืนยันไปแล้ว");
                else if (order.getItems().size() == 0)
                    MessageBox("ไม่มีรายการสินค้า");
                else if (order.getPay() == OrderPay.Transfer && order.getBank_id() <= 0)
                    MessageBox("เงินโอนยังไม่ได้เลือกธนาคาร");
                else {
                    AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new AlertDialog.Builder(ActOrderInputPayment.this, android.R.style.Theme_Material_Light_Dialog_Alert) : new AlertDialog.Builder(ActOrderInputPayment.this);
                    dialog.setTitle("ยืนยันใบสั่งขาย");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(true);
                    dialog.setMessage("ใบสั่งยืนยันแล้วจะไม่สามารถแก้ไขได้ คุณต้องการยืนยันใช่หรือไม่");
                    dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            order.setRemark(orderiteminputpayment_remark.getText().toString());
                            order.setPaid(Float.valueOf(orderiteminputpayment_paid.getText().toString().replace(",", "")));
                            order.setCharge(Float.valueOf(orderiteminputpayment_charge.getText().toString().replace(",", "")));
                            order.setRefund(Float.valueOf(orderiteminputpayment_refund.getText().toString().replace(",", "")));
                            String[] params = {
                                    String.valueOf(order.getId()),
                                    order.isShip() ? "1" : "0",
                                    String.valueOf(order.getPay()),
                                    String.valueOf(order.getRemark()),
                                    String.valueOf(order.getPaid()),
                                    String.valueOf(order.getCharge()),
                                    String.valueOf(order.getRefund()),
                                    String.valueOf(order.getBank_id())
                            };
                            new SqlQuery(ActOrderInputPayment.this, spUpdate, "{call " + Global.getDatabase(getApplicationContext()).getPrefix() + "setorderpay(?,?,?,?,?,?,?,?)}", params);
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
        new SqlQuery(ActOrderInputPayment.this, spGetInvoice, "{call " + Global.getDatabase(getApplicationContext()).getPrefix() + "getinvoice(?,?)}", new String[]{String.valueOf(order.getId()), String.valueOf(order.getWh_grp_Id())});
    }

    private void redrawOrder() {
        orderinput_cus.setText(order.getCus_name());
        orderiteminputpayment_no.setText(order.getNo());
        orderiteminputpayment_qty.setText(String.valueOf(order.getQty()));
        orderiteminputpayment_wgt.setText(String.valueOf(order.getWeight()));
        orderiteminputpayment_amt.setText(Global.formatMoney(order.getAmount()));
        switch_payship.setChecked(order.isShip());
        orderiteminputpayment_paid.setText(Global.formatMoney(order.getAmount()));
        orderiteminputpayment_refund.setText(Global.formatMoney(0));
        orderiteminputpayment_charge.setText(Global.formatMoney(0));

        if (order.getStat() == OrderStat.Confirm) {
            if (order.getPay() == OrderPay.Cash) {
                radio_paycash.setChecked(true);
            } else if (order.getPay() == OrderPay.Transfer) {
                radio_paytranfer.setChecked(true);
            } else if (order.getPay() == OrderPay.Credit) {
                radio_paycredit.setChecked(true);
            }
            orderiteminputpayment_bank.setText(order.getBank_name());
            orderiteminputpayment_remark.setText(order.getRemark());
            orderiteminputpayment_paid.setText(Global.formatMoney(order.getPaid()));
            orderiteminputpayment_refund.setText(Global.formatMoney(order.getRefund()));
            orderiteminputpayment_charge.setText(Global.formatMoney(order.getCharge()));

            orderiteminputpayment_paid.setEnabled(false);
            orderiteminputpayment_refund.setEnabled(false);
            orderiteminputpayment_remark.setEnabled(false);
            switch_payship.setEnabled(false);
            radio_paycash.setEnabled(false);
            radio_paytranfer.setEnabled(false);
            radio_paycredit.setEnabled(false);

        } else {
            order.setPay(OrderPay.Cash);
            orderiteminputpayment_paid.setSelectAllOnFocus(true);
            orderiteminputpayment_paid.requestFocus();
        }


    }

    private void calRefund() {
        float charge;
        if (order.getPay() == OrderPay.Credit) {
            charge = (float) ((order.getAmount() * 2) / 100.00);
        } else charge = 0;
        float paid = order.getAmount() + charge;
        float refund = paid - (order.getAmount() + charge);
        orderiteminputpayment_charge.setText(Global.formatMoney(charge));
        orderiteminputpayment_refund.setText(Global.formatMoney(refund));
        orderiteminputpayment_paid.setText(Global.formatMoney(paid));
        stop = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nologout, menu);
        if (menu != null) {
            menu.clear();
            for (int i = 0; i < invoices.size(); i++) {
                menu.add(0, i, Menu.NONE, "พิมพ์ใบเสร็จ: " + invoices.get(i).getNo());
            }
        }
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Invoice invoice = invoices.get(item.getItemId());
        webView = new WebView(ActOrderInputPayment.this);
        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("", "page finished loading " + url);
                createWebPrintJob(view, "BSRPOS Invoice:" + invoice.getNo());
            }
        });
        new SqlQuery(ActOrderInputPayment.this, spQueryInvoicePrint, "{call " + Global.getDatabase(getApplicationContext()).getPrefix() + "getinvoiceprint(?)}", new String[]{String.valueOf(invoice.getId())});
        return true;
    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException {
        if (tag == spUpdate) {
            if (rs != null && rs.next()) {
                SqlResult result = new SqlResult(rs);
                if (result.getMsg() == null) {
                    order.setStat(OrderStat.Confirm);
                    MessageBox("ยืนยันใบสั่งขายสำเร็จ");
                    getInvoices();
                } else MessageBox(result.getMsg());
            }
        } else if (tag == spGetInvoice) {
            while (rs != null && rs.next()) {
                Invoice i = new Invoice(rs.getInt("id"), rs.getString("no"), rs.getString("invoice_date"), rs.getString("cus_name"),
                        rs.getInt("qty"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getString("usr_name"),
                        OrderPay.valueOf(rs.getString("pay")), rs.getBoolean("ship"), rs.getString("remark"), rs.getInt("order_id"),
                        rs.getString("order_no"), rs.getFloat("paid"), rs.getFloat("charge"), rs.getFloat("refund"), OrderStat.valueOf(rs.getString("stat")));
                invoices.add(i);
                invalidateOptionsMenu();
            }

        } else if (tag == spQueryInvoicePrint) {
            if (rs != null && rs.next()) {
                String htmlDocument = rs.getString("html");
                webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && null != data) {
                order.setBank_id(data.getIntExtra("bank_id", -1));
                order.setBank_name(data.getStringExtra("bank_name"));
                orderiteminputpayment_bank.setText(order.getBank_name());
            }
        }
    }
}