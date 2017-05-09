package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.Model.RecordStat;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;

import java.util.ArrayList;
import java.util.List;

public class ActOrderInput extends ActBase {

    private Order order;
    private List<Product> products = new ArrayList<>();
    private AdpCustom<Product> adapProduct;

    private TextView orderinput_no, orderinput_qty, orderinput_wgt, orderinput_amt, orderinput_listtitle, orderinput_cus;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderinput);


        //region ORDER
        orderinput_cus = (TextView) findViewById(R.id.orderinput_cus);
        orderinput_no = (TextView) findViewById(R.id.orderinput_no);
        orderinput_qty = (TextView) findViewById(R.id.orderinput_qty);
        orderinput_wgt = (TextView) findViewById(R.id.orderinput_wgt);
        orderinput_amt = (TextView) findViewById(R.id.orderinput_amt);
        orderinput_listtitle = (TextView) findViewById(R.id.orderinput_listtitle);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            MessageBox("error");
            finish();
        }

        order = (Order) (bundle != null ? bundle.getSerializable("order") : null);
        Customer customer = (Customer) (bundle != null ? bundle.getSerializable("customer") : null);

        if (order == null && customer != null) {
            order = new Order(customer.getId(), customer.getName(), customer.isShip());
            setTitle("เปิดบิลใหม่@" + Global.wh_name);
        } else if (order != null) {
            order = OrderItem.retrieve(order);
            setTitle(order.getNo() + "@" + Global.wh_name);
        }
        redrawOrder();
        //endregion

        //region ORDERITEM
        final AdpCustom<OrderItem> adapOrderItem = new AdpCustom<OrderItem>(R.layout.listing_grid_orderitem, getLayoutInflater(), order.getItems()) {
            @Override
            protected void populateView(View v, OrderItem model) {

                TextView orderitem_no = (TextView) v.findViewById(R.id.orderitem_no);
                orderitem_no.setText(String.valueOf(model.getNo()));

                TextView orderitem_desc = (TextView) v.findViewById(R.id.orderitem_desc);
                orderitem_desc.setText(model.getProduct().getName());

                TextView orderitem_qty = (TextView) v.findViewById(R.id.orderitem_qty);
                orderitem_qty.setText(String.valueOf(String.valueOf(model.getPrice()) + "x" + model.getQty()));
            }
        };
        final ListView listOrderItem = (ListView) findViewById(R.id.list_order_item);
        listOrderItem.setAdapter(adapOrderItem);
        listOrderItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("orderItem", order.getItems().get(position));
                Intent intent = new Intent(ActOrderInput.this, ActOrderItemInput.class);
                intent.putExtras(bundle1);
                ActOrderInput.this.startActivity(intent);
            }
        });
        //endregion

        //region PRODUCT
        if (order.getStat() == OrderStat.New) {

            setSwipeRefresh(R.id.swipe_refresh, R.id.orderinput_product);

            adapProduct = new AdpCustom<Product>(R.layout.listing_grid_orderproduct, getLayoutInflater(), products) {
                @Override
                protected void populateView(View v, Product model) {

                    v.setBackgroundColor(Color.parseColor(model.getColor()));

                    TextView orderproduct_name = (TextView) v.findViewById(R.id.orderproduct_name);
                    orderproduct_name.setText(model.getName());

                    TextView orderproduct_price = (TextView) v.findViewById(R.id.orderproduct_price);
                    orderproduct_price.setText(String.valueOf(model.getPrice()) + " ฿");

                    if (model.isStepPrice()) {
                        orderproduct_price.setTextColor(Color.RED);
                    } else {
                        orderproduct_price.setTextColor(Color.BLACK);
                    }
                    if (searchString != null)
                        SetTextSpan(searchString, model.getName(), orderproduct_name);
                    redrawProduct(model.getStock(), v);
                }
            };
            ListView listProduct = (ListView) findViewById(R.id.orderinput_product);
            listProduct.setAdapter(adapProduct);
            listProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Product p = products.get(position);
                    if (p.getStock() > 0) {
                        OrderItem item = order.findItem(p);
                        if (item == null) {
                            item = new OrderItem(order, order.getItems().size() + 1, p);
                            order.getItems().add(item);
                        }
                        item.addQty(1);
                        listOrderItem.setSelection(order.getItems().indexOf(item));
                        adapOrderItem.notifyDataSetChanged();
                        p.setStock(p.getStock() - 1);
                        ActOrderInput.this.redrawProduct(p.getStock(), view);
                        ActOrderInput.this.redrawOrder();
                    }
                }
            });

            refresh();
            AddVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, products, adapProduct);
        }
        //endregion

        //region SAVE
        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);
        if (order.getStat() == OrderStat.New) {
            bt_cmd_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SqlResult result = order.save();
                    ActOrderInput.this.redrawOrder();
                    ActOrderInput.this.MessageBox(result.getMsg() == null ? "บันทึกสำเร็จ" : result.getMsg());
                }
            });

        } else bt_cmd_save.setEnabled(false);
        //endregion

        //region PAY
        Button bt_pay = (Button) findViewById(R.id.bt_pay);
        bt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("order", order);
                Intent intent = new Intent(ActOrderInput.this, ActOrderInputPayment.class);
                intent.putExtras(bundle1);
                ActOrderInput.this.startActivity(intent);
            }
        });
        //endregion
    }

    private void redrawProduct(int stock, View v) {
        TextView orderproduct_stock = (TextView) v.findViewById(R.id.orderproduct_stock);
        orderproduct_stock.setText("[" + String.valueOf(stock) + "]");

        TextView orderproduct_name = (TextView) v.findViewById(R.id.orderproduct_name);
        if (stock == 0) {
            orderproduct_name.setPaintFlags(orderproduct_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            orderproduct_name.setPaintFlags(orderproduct_name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private void redrawOrder() {
        orderinput_cus.setText(String.valueOf(order.getCus_name()));
        orderinput_no.setText(order.getNo());
        orderinput_qty.setText(String.valueOf(order.getQty()));
        orderinput_wgt.setText(String.valueOf(order.getWeight()));
        orderinput_amt.setText(String.valueOf(order.getAmount()));
        orderinput_listtitle.setText("รายการสินค้า(" + String.valueOf(order.getItems().size()) + ")");
    }

    @Override
    public void onBackPressed() {
        if (order.getRecordStat() == RecordStat.NULL)
            backPressed(ActOrder.class);
        else
            backPressed(ActOrder.class, "รายการยังไม่เซฟ", "เอกสารยังไม่เซฟ ท่านต้องการออกโดยไม่เซฟหรือไม่?");
    }

    @Override
    public void refresh() {
        products = Product.retrieve(products);
        if (adapProduct != null) adapProduct.notifyDataSetChanged();
    }

}