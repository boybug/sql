package com.newit.bsrpos_sql.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.newit.bsrpos_sql.Model.Customer;
import com.newit.bsrpos_sql.Model.FbStock;
import com.newit.bsrpos_sql.Model.Global;
import com.newit.bsrpos_sql.Model.Order;
import com.newit.bsrpos_sql.Model.OrderItem;
import com.newit.bsrpos_sql.Model.OrderStat;
import com.newit.bsrpos_sql.Model.Product;
import com.newit.bsrpos_sql.Model.RecordStat;
import com.newit.bsrpos_sql.Model.SqlResult;
import com.newit.bsrpos_sql.Model.StepPrice;
import com.newit.bsrpos_sql.R;
import com.newit.bsrpos_sql.Util.AdpCustom;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActOrderInput extends ActBase {

    private Order order;
    private List<Product> products = new ArrayList<>();
    private List<FbStock> fbStocks = new ArrayList<>();
    private AdpCustom<Product> adapProduct;
    private int selectedIndex;
    private AdpCustom<OrderItem> adapOrderItem;
    private TextView orderinput_no, orderinput_qty, orderinput_wgt, orderinput_amt, orderinput_listtitle, orderinput_cus;

    private final int spQueryOrderItem = 1;
    private final int spQueryProduct = 2;
    private final int spQueryOrderItemPrice = 3;
    private final int spQueryOrderPrint = 4;

    private DrawerLayout drawer;
    private DatabaseReference fb = FirebaseDatabase.getInstance().getReference().child(Global.getFbStockPath());
    private WebView webView;


    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderinput);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

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
            setTitle("เปิดใบสั่งใหม่@" + Global.wh_grp_name);
        } else if (order != null) {
            showProgressDialog();
            new SqlQuery(ActOrderInput.this, spQueryOrderItem, "{call " + Global.database.getPrefix() + "getorderitem(?)}", new String[]{String.valueOf(order.getId())});

            setTitle("ใบสั่งขาย " + order.getNo() + "@" + Global.wh_grp_name);
        }
        redrawOrder();
        //endregion

        //region ORDERITEM
        adapOrderItem = new AdpCustom<OrderItem>(R.layout.listing_grid_orderitem, getLayoutInflater(), order.getItems()) {
            @Override
            protected void populateView(View v, OrderItem model) {
                TextView orderitem_no = (TextView) v.findViewById(R.id.orderitem_no);
                TextView orderitem_desc = (TextView) v.findViewById(R.id.orderitem_desc);
                TextView orderitem_qty = (TextView) v.findViewById(R.id.orderitem_qty);
                orderitem_no.setText(String.valueOf(model.getNo()));
                orderitem_desc.setText(model.getProduct().getName());
                orderitem_qty.setText(String.valueOf(String.valueOf(model.getPrice()) + "x" + model.getQty()));
            }
        };
        final ListView listOrderItem = (ListView) findViewById(R.id.list_order_item);
        listOrderItem.setAdapter(adapOrderItem);
        if (order.getStat() == OrderStat.New) {
            listOrderItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("orderItem", order.getItems().get(position));
                    Intent intent = new Intent(ActOrderInput.this, ActOrderItemInput.class);
                    intent.putExtras(bundle1);
                    selectedIndex = position;
                    ActOrderInput.this.startActivityForResult(intent, 3);
                }
            });
            listOrderItem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final OrderItem item = order.getItems().get(position);
                    if (order.getUsr_id() != Global.user.getId())
                        MessageBox("ไม่สามารถแก้ไขรายการคนอื่นได้");
                    else if (order.getStat() == OrderStat.Confirm)
                        MessageBox("เอกสารยืนยันแล้วลบไม่ได้");
                    else {
                        AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new AlertDialog.Builder(ActOrderInput.this, android.R.style.Theme_Material_Light_Dialog_Alert) : new AlertDialog.Builder(ActOrderInput.this);
                        dialog.setTitle("ยืนยันการลบ");
                        dialog.setIcon(R.mipmap.ic_launcher);
                        dialog.setCancelable(true);
                        dialog.setMessage("ท่านต้องการลบรายการนี้หรือไม่?");
                        dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog12, int which) {
                                //ย้ายไปพักไว้ก่อน
                                if (item.getId() > 0) order.getDeletingItems().add(item);
                                //update stock
                                FbStock f = item.getProduct().getFbstock();
                                fb.child(f.getKey()).child("reserve").setValue(f.getReserve() - item.getQty());
                                //ลบออกจาก array
                                order.getItems().remove(item);
                                order.updateHeader();
                                //rerun no
                                for (int i = 1; i <= order.getItems().size(); i++)
                                    order.getItems().get(i - 1).setNo(i);
                                //redraw
                                adapOrderItem.notifyDataSetChanged();
                                adapProduct.notifyDataSetChanged();
                                redrawOrder();
                            }
                        });
                        dialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int which) {
                                dialog1.cancel();
                            }
                        });
                        dialog.show();
                    }
                    return true;
                }
            });
        }
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
                    redrawProduct(model.getRemaining(), v);
                }
            };
            addVoiceSearch(R.id.search_txt, R.id.search_btn, R.id.search_clear, products, adapProduct);
            ListView listProduct = (ListView) findViewById(R.id.orderinput_product);
            listProduct.setAdapter(adapProduct);
            listProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (order.getUsr_id() != Global.user.getId())
                        MessageBox("ไม่สามารถแก้ไขรายการคนอื่นได้");
                    else {
                        Product p = adapProduct.getModels().get(position);
                        if (p.getRemaining() > 0) {
                            OrderItem item = order.findItem(p);
                            if (item == null) {
                                item = new OrderItem(order, order.getItems().size() + 1, p);
                                new SqlQuery(ActOrderInput.this, spQueryOrderItemPrice, "{call " + Global.database.getPrefix() + "getstepprice(?,?)}", new String[]{String.valueOf(p.getId()), String.valueOf(p.getWh_Id())}, item);
                                order.getItems().add(item);
                            }
                            item.addQty(1);
                            adapOrderItem.notifyDataSetChanged();
                            listOrderItem.setSelection(order.getItems().indexOf(item));
                            if (p.getFbstock() == null) {
                                updateFbStock(p);
                                if (p.getFbstock() == null) {
                                    FbStock fbStock = new FbStock();
                                    fbStock.setReserve(1);
                                    fbStock.setProd_id(p.getId());
                                    fbStock.setWh_id(p.getWh_Id());
                                    String key = fb.push().getKey();
                                    fb.child(key).setValue(fbStock);
                                }
                            } else {
                                FbStock f = p.getFbstock();
                                f.setReserve(f.getReserve() + 1);
                                fb.child(p.getFbstock().getKey()).child("reserve").setValue(f.getReserve());
                            }
                            ActOrderInput.this.redrawProduct(p.getRemaining(), view);
                            ActOrderInput.this.redrawOrder();
                        }
                    }
                }
            });
            refresh();
        } else {
            DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        //endregion

        //region SAVE
        Button bt_cmd_save = (Button) findViewById(R.id.bt_cmd_save);
        bt_cmd_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getUsr_id() != Global.user.getId())
                    MessageBox("ไม่สามารถบันทึกรายการคนอื่นได้");
                else if (order.getStat() == OrderStat.Confirm)
                    MessageBox("ไม่สามารถบันทึกรายการที่ยืนยันแล้ว");
                else {
                    SqlResult result = order.save(ActOrderInput.this);
                    ActOrderInput.this.redrawOrder();
                    adapOrderItem.notifyDataSetChanged();
                    ActOrderInput.this.MessageBox(result.getMsg() == null ? "บันทึกสำเร็จ" : result.getMsg());
                }
            }
        });
        //endregion

        //region add_item
        Button bt_add_item = (Button) findViewById(R.id.bt_add_item);
        bt_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getStat() != OrderStat.Confirm) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            }
        });
        //endregion

        //region Firebase
        ChildEventListener fbStockListner = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FbStock fbStock = dataSnapshot.getValue(FbStock.class);
                fbStock.setKey(dataSnapshot.getKey());
                fbStocks.add(fbStock);
                mergeList(fbStock, false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FbStock fbStock = dataSnapshot.getValue(FbStock.class);
                fbStock.setKey(dataSnapshot.getKey());
                for (FbStock f : fbStocks) {
                    if (Objects.equals(f.getKey(), fbStock.getKey())) {
                        f.setReserve(fbStock.getReserve());
                        mergeList(fbStock, false);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                FbStock fbStock = dataSnapshot.getValue(FbStock.class);
                fbStock.setKey(dataSnapshot.getKey());
                for (FbStock f : fbStocks) {
                    if (Objects.equals(f.getKey(), fbStock.getKey())) {
                        fbStocks.remove(f);
                        mergeList(fbStock, true);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        fb.addChildEventListener(fbStockListner);

        //endregion
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra("FINISH", false))
                finish();
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            int delta = data.getIntExtra("DELTA", 0);
            OrderItem oi = order.getItems().get(selectedIndex);
            oi.addQty(delta);
            adapOrderItem.notifyDataSetChanged();
            ActOrderInput.this.redrawOrder();
        }
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
        else {
            AlertDialog.Builder dialog = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert) : new AlertDialog.Builder(this);
            dialog.setTitle("รายการยังไม่เซฟ");
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setCancelable(true);
            dialog.setMessage("เอกสารยังไม่เซฟ ท่านต้องการออกโดยไม่เซฟหรือไม่?");
            dialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog12, int which) {
                    for (OrderItem item : order.getItems()) {
                        if (item.getDelta() != 0) {
                            FbStock f = item.getProduct().getFbstock();
                            if (f != null) {
                                fb.child(f.getKey()).child("reserve").setValue(f.getReserve() - item.getDelta());
                            }
                        }
                    }
                    for (OrderItem item : order.getDeletingItems()) {
                        FbStock f = item.getProduct().getFbstock();
                        if (f != null) {
                            fb.child(f.getKey()).child("reserve").setValue(f.getReserve() + item.getQty());
                        }
                    }
                    backPressed(ActOrder.class);

                }
            });
            dialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog1, int which) {
                    dialog1.cancel();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void refresh() {
        if (order.getStat() == OrderStat.New) {
            new SqlQuery(ActOrderInput.this, spQueryProduct, "{call " + Global.database.getPrefix() + "getproduct(?)}", new String[]{String.valueOf(Global.wh_Grp_Id)});
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nologout, menu);
        menu.add(0, 1, Menu.NONE, "พิมพ์ใบหยิบสินค้า");
        menu.add(0, 2, Menu.NONE, "ชำระเงินและยืนยัน");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (order.getItems().size() == 0) {
                MessageBox("ไม่มีรายการขาย ไม่สามารถพิมพ์ใบเบิกได้");
            } else if (Objects.equals(order.getNo(), null) || order.getRecordStat() != RecordStat.NULL) {
                MessageBox("เอกสารมีการเปลี่ยนแปลง กรุณาบันทึกก่อนการพิมพ์ใบเบิก");
            } else {
                webView = new WebView(ActOrderInput.this);
                webView.setWebViewClient(new WebViewClient() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        createWebPrintJob(view, "BSRPOS Order:" + order.getNo());
                    }
                });
                new SqlQuery(ActOrderInput.this, spQueryOrderPrint, "{call " + Global.database.getPrefix() + "getorderprint(?)}", new String[]{String.valueOf(order.getId())});
            }
        } else if (item.getItemId() == 2) {
            if (order.getItems().size() == 0) {
                MessageBox("ไม่มีรายการขาย ไม่สามารถชำระเงินได้");
            } else if (Objects.equals(order.getNo(), null) || order.getRecordStat() != RecordStat.NULL) {
                MessageBox("เอกสารมีการเปลี่ยนแปลง กรุณาบันทึกก่อนการชำระเงิน");
            } else {
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("order", order);
                Intent intent = new Intent(ActOrderInput.this, ActOrderInputPayment.class);
                intent.putExtras(bundle1);
                ActOrderInput.this.startActivityForResult(intent, 2);
            }
        }
        return true;
    }

    @Override
    public void queryReturn(ResultSet rs, int tag, Object caller) throws SQLException {
        if (tag == spQueryOrderItem) {
            order.getItems().clear();
            while (rs != null && rs.next()) {
                Product p = new Product(rs.getInt("prod_Id"), rs.getString("prod_name"), rs.getInt("stock"), rs.getFloat("weight"), rs.getString("color"), rs.getBoolean("stepprice"), rs.getFloat("price"), rs.getInt("uom_id"), rs.getInt("wh_Id"));
                updateFbStock(p);
                OrderItem item = new OrderItem(order, rs.getInt("id"), rs.getInt("no"), p, rs.getInt("qty"), rs.getFloat("price"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getInt("uom_id"), rs.getInt("wh_Id"));
                new SqlQuery(ActOrderInput.this, spQueryOrderItemPrice, "{call " + Global.database.getPrefix() + "getstepprice(?,?)}", new String[]{String.valueOf(p.getId()), String.valueOf(p.getWh_Id())}, item);
                order.getItems().add(item);
            }
            adapOrderItem.notifyDataSetChanged();
            redrawOrder();
            hideProgressDialog();

        } else if (tag == spQueryProduct) {
            products.clear();
            while (rs != null && rs.next()) {
                Product p = new Product(rs.getInt("prod_Id"), rs.getString("prod_name"), rs.getInt("stock"), rs.getFloat("weight"), rs.getString("color"), rs.getBoolean("stepprice"), rs.getFloat("price"), rs.getInt("uom_id"), rs.getInt("wh_Id"));
                updateFbStock(p);
                products.add(p);
            }
            adapProduct.notifyDataSetChanged();

        } else if (tag == spQueryOrderItemPrice) {
            while (rs != null && rs.next()) {
                StepPrice p = new StepPrice(rs.getInt("from"), rs.getInt("to"), rs.getFloat("price"));
                OrderItem item = ((OrderItem) caller);
                item.getPrices().add(p);
            }
        } else if (tag == spQueryOrderPrint) {
            if (rs != null && rs.next()) {
                String htmlDocument = rs.getString("html");
                webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
            }
        }
    }

    private void updateFbStock(Product p) {
        for (FbStock f : fbStocks) {
            if (f.getProd_id() == p.getId() && f.getWh_id() == p.getWh_Id()) {
                p.setFbstock(f);
                break;
            }
        }
    }

    public void mergeList(FbStock fbstock, boolean isDelete) {
        if (order.getStat() == OrderStat.New) {
            for (Product p : products) {
                if (p.getId() == fbstock.getProd_id() && p.getWh_Id() == fbstock.getWh_id()) {
                    p.setFbstock(isDelete ? null : fbstock);
                    adapProduct.notifyDataSetChanged();
                    break;
                }
            }
        }
        for (OrderItem i : order.getItems()) {
            if (i.getProduct() != null && i.getProduct().getId() == fbstock.getProd_id() && i.getWh_Id() == fbstock.getWh_id()) {
                i.getProduct().setFbstock(isDelete ? null : fbstock);
                break;
            }
        }
        if (order.getStat() == OrderStat.New) {
            if (txt_search.getText().toString().length() > 0) {
                for (Product p : adapProduct.getModels()) {
                    if (p.getId() == fbstock.getProd_id() && p.getWh_Id() == fbstock.getWh_id()) {
                        p.setFbstock(isDelete ? null : fbstock);
                        adapProduct.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }
}