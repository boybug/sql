package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.Model.RecordStat;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActOrderInput extends ActBase {

    private Order order;
    private List<Customer> customers = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private TextView orderinput_no, orderinput_qty, orderinput_wgt, orderinput_amt, orderinput_listtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderinput);


        //region ORDER
        orderinput_no = (TextView) findViewById(R.id.orderinput_no);
        orderinput_qty = (TextView) findViewById(R.id.orderinput_qty);
        orderinput_wgt = (TextView) findViewById(R.id.orderinput_wgt);
        orderinput_amt = (TextView) findViewById(R.id.orderinput_amt);
        orderinput_listtitle = (TextView) findViewById(R.id.orderinput_listtitle);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            setTitle("เปิดบิลใหม่@" + Global.wh_name);
            order = new Order();

        } else {
            order = (Order) bundle.getSerializable("order");
            redrawOrder();

            try {
                ResultSet rs = SqlServer.execute("{call POS.dbo.getorderitem(" + order.getId() + ")}");
                while (rs.next()) {
                    Product p = null;
                    try {
                        ResultSet rs1 = SqlServer.execute("{call POS.dbo.getproductbyid(" + Integer.valueOf(rs.getInt("prod_id")) + "," + order.getWh_id() + ")}");
                        if (rs1.next()) {
                            p = new Product(rs.getInt("prod_Id"), rs.getString("prod_name"),
                                    rs.getInt("stock"), rs.getFloat("weight"),
                                    rs.getString("color"), rs.getBoolean("stepprice"),
                                    rs.getFloat("price"), rs.getInt("uom_id"));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    OrderItem item = new OrderItem(order, rs.getInt("id"), rs.getInt("no"), p, rs.getInt("qty"), rs.getFloat("price"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getInt("uom_id"));
                    order.getItems().add(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            setTitle(order.getNo() + "@" + Global.wh_name);
        }
        //endregion


        //region ORDERITEM
        AdpCustom<OrderItem> adapOrderItem = new AdpCustom<OrderItem>(R.layout.listing_grid_orderitem, getLayoutInflater(), order.getItems()) {
            @Override
            protected void populateView(View v, OrderItem model) {

                TextView orderitem_no = (TextView) v.findViewById(R.id.orderitem_no);
                orderitem_no.setText(String.valueOf(model.getNo()));

                TextView orderitem_desc = (TextView) v.findViewById(R.id.orderitem_desc);
                orderitem_desc.setText(model.getProduct().getName());

                TextView orderitem_qty = (TextView) v.findViewById(R.id.orderitem_qty);
                orderitem_qty.setText(String.valueOf(model.getQty()) + "x" + String.valueOf(model.getPrice()));
            }
        };
        ListView listOrderItem = (ListView) findViewById(R.id.list_order_item);
        listOrderItem.setAdapter(adapOrderItem);
        listOrderItem.setOnItemClickListener((parent, view, position, id) -> {
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("orderItem", order.getItems().get(position));
            Intent intent = new Intent(ActOrderInput.this, ActOrderItemInput.class);
            intent.putExtras(bundle1);
            startActivity(intent);
        });
        //endregion


        //region CUSTOMER
        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getcus(" + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                Customer c = new Customer(rs.getInt("cus_Id"), rs.getString("cus_name"), rs.getString("cus_addr"), rs.getString("cus_tel"), rs.getBoolean("cus_ship"));
                customers.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AdpCustom<Customer> adapCustomer = new AdpCustom<Customer>(android.R.layout.simple_spinner_item, getLayoutInflater(), customers) {
            @Override
            protected void populateView(View v, Customer cus) {
                TextView text1 = (TextView) v.findViewById(android.R.id.text1);
                text1.setText(cus.getName());
            }
        };
        Spinner orderinput_cus = (Spinner) findViewById(R.id.orderinput_cus);
        orderinput_cus.setAdapter(adapCustomer);
        //endregion


        //region PRODUCT
        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getproduct(" + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                Product p = new Product(rs.getInt("prod_Id"), rs.getString("prod_name"),
                        rs.getInt("stock"), rs.getFloat("weight"),
                        rs.getString("color"), rs.getBoolean("stepprice"),
                        rs.getFloat("price"), rs.getInt("uom_id"));
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AdpCustom<Product> adapProduct = new AdpCustom<Product>(R.layout.listing_grid_orderproduct, getLayoutInflater(), products) {
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

                redrawProduct(model.getStock(), v);
            }
        };
        ListView listProduct = (ListView) findViewById(R.id.orderinput_product);
        listProduct.setAdapter(adapProduct);
        listProduct.setOnItemClickListener((parent, view, position, id) -> {
            Product p = products.get(position);
            if (p.getStock() > 0) {
                OrderItem item = order.findItem(p);
                if (item == null) {
                    item = new OrderItem(order, order.getItemCount() + 1, p);
                    order.getItems().add(item);
                }
                item.addQty(1);
                listOrderItem.setSelection(order.getItems().indexOf(item));
                adapOrderItem.notifyDataSetChanged();
                p.setStock(p.getStock() - 1);
                redrawProduct(p.getStock(), view);
                redrawOrder();
            }
        });
        //endregion

        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);
        bt_cmd_save.setOnClickListener(v -> {
            if(order.getRecordStat() != RecordStat.NULL)
            try {
                ResultSet rs = SqlServer.execute("{call POS.dbo.setorder(" +
                        String.valueOf(order.getId()) + "," +
                        order.getNo() + "," +
                        String.valueOf(order.getDate()) + "," +
                        String.valueOf(order.getCus_id()) + "," +
                        String.valueOf(order.getStat()) + "," +
                        String.valueOf(order.getWh_id()) + "," +
                        String.valueOf(order.getUsr_id()) + "," +
                        String.valueOf(order.getQty()) + "," +
                        String.valueOf(order.getAmount()) + "," +
                        String.valueOf(order.getWeight()) + "," +
                        String.valueOf(order.getRecordStat()) +
                        ")}");
                if (rs.next()) {
                    int iden = rs.getInt("Iden");
                    if(iden > 0) {
                        order.setId(iden);
                        order.setRecordStat(RecordStat.NULL);

                        for(OrderItem item : order.getItems())
                        {
                            item.getOrder().setId(order.getId());
                            if(item.getRecordStat() != RecordStat.NULL)
                            {
                                try {
                                    ResultSet rs1 = SqlServer.execute("{call POS.dbo.setorderitem(" +
                                            String.valueOf(item.getOrder().getId()) + "," +
                                            String.valueOf(item.getId()) + "," +
                                            String.valueOf(item.getNo()) + "," +
                                            String.valueOf(item.getProduct().getId()) + "," +
                                            String.valueOf(item.getQty()) + "," +
                                            String.valueOf(item.getPrice()) + "," +
                                            String.valueOf(item.getAmount()) + "," +
                                            String.valueOf(item.getWeight()) + "," +
                                            String.valueOf(item.getUom_id()) + "," +
                                            String.valueOf(item.getRecordStat()) +
                                            ")}");
                                    if (rs1.next()) {
//                                        listOrderItem.notify();
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                    else MessageBox(rs.getString("Msg"));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }


    public void redrawProduct(int stock, View v) {
        TextView orderproduct_stock = (TextView) v.findViewById(R.id.orderproduct_stock);
        orderproduct_stock.setText("[" + String.valueOf(stock) + "]");

        TextView orderproduct_name = (TextView) v.findViewById(R.id.orderproduct_name);
        if (stock == 0) {
            orderproduct_name.setPaintFlags(orderproduct_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            orderproduct_name.setPaintFlags(orderproduct_name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public void redrawOrder() {
        orderinput_no.setText(order.getNo());
        orderinput_qty.setText(String.valueOf(order.getQty()));
        orderinput_wgt.setText(String.valueOf(order.getWeight()));
        orderinput_amt.setText(String.valueOf(order.getAmount()));
        orderinput_listtitle.setText("รายการสินค้า(" + String.valueOf(order.getItemCount()) + ")");
    }
}