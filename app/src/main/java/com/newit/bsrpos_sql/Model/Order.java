package com.newit.bsrpos_sql.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order extends ModelBase implements Serializable {
    private int id;
    private String no;
    private Date date;
    private int cus_id;
    private int wh_id;
    private String cus_name;
    private OrderStat stat;
    private List<OrderItem> items;
    private int itemCount;
    private int qty;
    private float weight;
    private float amount;
    private int usr_id;
    private String usr_name;
    private static final long serialVersionUID = 2L;

    public Order() {
        super(true);
        date = new Date();
        itemCount = 0;
        qty = 0;
        weight = 0;
        amount = 0;
        usr_id = Global.usr_Id;
        usr_name = Global.usr_name;
        items = new ArrayList<>();
        stat = OrderStat.New;
        wh_id = Global.wh_Id;
    }

    public Order(int id, String no, Date date, int cus_id, String cus_name, int wh_id, OrderStat stat, int qty, float weight, float amount, int usr_Id, String usr_name) {
        super(false);
        this.id = id;
        this.no = no;
        this.date = date;
        this.cus_id = cus_id;
        this.cus_name = cus_name;
        this.wh_id = wh_id;
        this.stat = stat;
        this.qty = qty;
        this.weight = weight;
        this.amount = amount;
        this.usr_id = usr_Id;
        this.usr_name = usr_name;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
    }

    public String getNo() {
        return no;
    }

    public Date getDate() {
        return date;
    }

    public int getCus_id() {
        return cus_id;
    }

    public String getCus_name() {
        return cus_name;
    }

    public void setCus_name(String cus_name) {
        this.cus_name = cus_name;
    }

    public OrderStat getStat() {
        return stat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStat(OrderStat stat) {
        this.stat = stat;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getUsr_id() {
        return usr_id;
    }

    public String getUsr_name() {
        return usr_name;
    }

    public OrderItem findItem(Product p) {
        for (OrderItem item : items) {
            if (item.getProduct().getId() == p.getId())
                return item;
        }
        return null;
    }

    public int getWh_id() {
        return wh_id;
    }

    public void setWh_id(int wh_id) {
        this.wh_id = wh_id;
    }
}
