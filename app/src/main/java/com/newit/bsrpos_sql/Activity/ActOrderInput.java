package com.newit.bsrpos_sql.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.Model.RecordStat;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlServer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActOrderInput extends ActBase {

    private Order order;
    private List<Product> products = new ArrayList<>();
    private List<Product> backup;
    private String searchString;

    private TextView orderinput_no, orderinput_qty, orderinput_wgt, orderinput_amt, orderinput_listtitle,orderinput_cus;

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

        order = (Order) bundle.getSerializable("order");
        Customer customer = (Customer) bundle.getSerializable("customer");

        if (order == null && customer != null) {
            setTitle("เปิดบิลใหม่@" + Global.wh_name);
            order = new Order(customer.getId(), customer.getName(), customer.isShip());
            redrawOrder();
        } else if (order != null && order.getStat().toString() == "New") {
            redrawOrder();
            try {
                ResultSet rs = SqlServer.execute("{call POS.dbo.getorderitem(" + Integer.valueOf(order.getId()) + ")}");
                while (rs.next()) {
                    Product p = null;
                    try {
                        ResultSet rs1 = SqlServer.execute("{call POS.dbo.getproductbyid(" +
                                                            Integer.valueOf(rs.getInt("prod_id")) + "," +
                                                            Integer.valueOf(order.getWh_id())+ "," +
                                                            Integer.valueOf(rs.getInt("uom_id")) +
                                                            ")}");
                        while (rs1.next()) {
                            p = new Product(rs1.getInt("prod_Id"), rs1.getString("prod_name"),
                                    rs1.getInt("stock"), rs1.getFloat("weight"),
                                    rs1.getString("color"), rs1.getBoolean("stepprice"),
                                    rs1.getFloat("price"), rs1.getInt("uom_id"));
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
                orderitem_qty.setText(String.valueOf(String.valueOf(model.getPrice()) + "x" + model.getQty()));
            }
        };
        ListView listOrderItem = (ListView) findViewById(R.id.list_order_item);
        listOrderItem.setAdapter(adapOrderItem);
        listOrderItem.setOnItemClickListener((parent, view, position, id) ->

        {
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("orderItem", order.getItems().get(position));
            Intent intent = new Intent(ActOrderInput.this, ActOrderItemInput.class);
            intent.putExtras(bundle1);
            startActivity(intent);
        });
        //endregion

        //region PRODUCT
        try

        {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getproduct(" + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                Product p = new Product(rs.getInt("prod_Id"), rs.getString("prod_name"),
                        rs.getInt("stock"), rs.getFloat("weight"),
                        rs.getString("color"), rs.getBoolean("stepprice"),
                        rs.getFloat("price"), rs.getInt("uom_id"));
                products.add(p);
            }
        } catch (
                SQLException e)

        {
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
                if(searchString != null)SetTextSpan(searchString,model.getName(),orderproduct_name);
                redrawProduct(model.getStock(), v);
            }
        };
        ListView listProduct = (ListView) findViewById(R.id.orderinput_product);
        listProduct.setAdapter(adapProduct);
        listProduct.setOnItemClickListener((parent, view, position, id) ->

        {
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

        //region SAVE
        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);
        bt_cmd_save.setOnClickListener(v -> {
            int ship = 0;
            if(order.isShip()) ship = 1 ;
            if (order.getRecordStat() != RecordStat.NULL)
                try {
                    ResultSet rs = SqlServer.execute("{call POS.dbo.setorder(" +
                            String.valueOf(order.getId()) + "," +
                            String.valueOf(order.getCus_id()) + ",'" +
                            String.valueOf(order.getStat()) + "'," +
                            String.valueOf(order.getWh_id()) + "," +
                            String.valueOf(order.getUsr_id()) + "," +
                            String.valueOf(order.getQty()) + "," +
                            String.valueOf(order.getAmount()) + "," +
                            String.valueOf(order.getWeight()) + ",'" +
                            String.valueOf(order.getRecordStat()) + "'," +
                            Integer.valueOf(ship) +
                            ")}");
                    if (rs.next()) {
                        int iden = rs.getInt("Iden");
                        if (iden > 0) {
                            order.setId(iden);
                            if (order.getRecordStat() == RecordStat.I)
                            {
                                order.setNo(rs.getString("order_no"));
                                redrawOrder();
                            }
                            order.setRecordStat(RecordStat.NULL);

                            for (OrderItem item : order.getItems()) {
                                item.getOrder().setId(order.getId());
                                if (item.getRecordStat() != RecordStat.NULL) {
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
                                                String.valueOf(item.getUom_id()) + ",'" +
                                                String.valueOf(item.getRecordStat()) +
                                                "')}");
                                        if (rs1.next()) {
                                            int iden1 = rs.getInt("Iden");
                                            if (iden1 > 0) {
                                                item.setId(iden1);
                                                item.setRecordStat(RecordStat.NULL);
                                            } else MessageBox(rs.getString("Msg"));
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else MessageBox(rs.getString("Msg"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        });
        //endregion

        ClearSearch(R.id.search_txt, R.id.clear_btn);
        AddVoiceSearch(R.id.search_txt, R.id.search_btn);
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchString = s.toString().toLowerCase(Locale.getDefault());
                List<Product> filtered = new ArrayList<>();
                for (Product p : products) {
                    if (p.getName().contains(searchString))
                        filtered.add(p);
                }
                if (backup == null)
                    backup = new ArrayList<>(products);
                adapProduct.setModels(filtered);
                adapProduct.notifyDataSetChanged();
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
        orderinput_cus.setText(String.valueOf(order.getCus_name()));
        orderinput_no.setText(order.getNo());
        orderinput_qty.setText(String.valueOf(order.getQty()));
        orderinput_wgt.setText(String.valueOf(order.getWeight()));
        orderinput_amt.setText(String.valueOf(order.getAmount()));
        orderinput_listtitle.setText("รายการสินค้า(" + String.valueOf(order.getItemCount()) + ")");
    }
}