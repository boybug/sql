package com.newit.bsrpos_sql.Model;


import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Invoice extends ModelBase {

    private static final long serialVersionUID = 7L;
    private int id;
    private String no;
    private final String date;
    private final String cus_name;
    private List<InvoiceItem> items = new ArrayList<>();
    private int qty;
    private float weight;
    private float amount;
    private final String usr_name;
    private OrderPay pay;
    private boolean ship;
    private String remark;
    private int order_Id;
    private String order_no;

    public Invoice(int id, String no, String date, String cus_name, int qty, float weight,
                   float amount, String usr_name, OrderPay pay, boolean ship, String remark, int order_Id, String order_no) {
        super(false);
        this.id = id;
        this.no = no;
        this.date = date;
        this.cus_name = cus_name;
        this.qty = qty;
        this.weight = weight;
        this.amount = amount;
        this.usr_name = usr_name;
        this.pay = pay;
        this.ship = ship;
        this.remark = remark;
        this.order_Id = order_Id;
        this.order_no = order_no;
    }

    private void queryInvoiceItems() {
        try {
            ResultSet rs = SqlQuery.executeWait("{call " + Global.database.getPrefix() + "getinvoiceitem(?)}", new String[]{String.valueOf(id)});
            while (rs != null && rs.next()) {
                InvoiceItem item = new InvoiceItem(rs.getInt("no"), rs.getString("prod_name"), rs.getInt("qty"), rs.getFloat("price"), rs.getFloat("weight"), rs.getFloat("amount"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String getNo() {
        return no;
    }

    public List<InvoiceItem> getItems() {
        //lazy load
        if (items.size() == 0) queryInvoiceItems();
        return items;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getCus_name() {
        return cus_name;
    }

    public int getQty() {
        return qty;
    }

    public float getWeight() {
        return weight;
    }

    public float getAmount() {
        return amount;
    }

    public String getUsr_name() {
        return usr_name;
    }

    public OrderPay getPay() {
        return pay;
    }

    public boolean isShip() {
        return ship;
    }

    public String getRemark() {
        return remark;
    }

    public int getOrder_Id() {
        return order_Id;
    }

    public String getOrder_no() {
        return order_no;
    }

    @Override
    public String getSearchString() {
        return no;
    }
}
