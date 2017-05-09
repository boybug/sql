package com.newit.bsrpos_sql.Model;

import com.newit.bsrpos_sql.Util.SqlServer;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order extends ModelBase implements Serializable {
    private static final long serialVersionUID = 2L;
    private int id;
    private String no;
    private final String date;
    private final int wh_id;
    private final int cus_id;
    private final String cus_name;
    private OrderStat stat;
    private List<OrderItem> items = new ArrayList<>();
    private int itemCount;
    private int qty;
    private float weight;
    private float amount;
    private final int usr_id;
    private final String usr_name;
    private String pay;
    private boolean ship;
    private String remark;

    public Order(int cus_id, String cus_name, boolean ship) {
        super(true);
        date = new Date().toString();
        itemCount = 0;
        qty = 0;
        weight = 0;
        amount = 0;
        this.cus_id = cus_id;
        this.cus_name = cus_name;
        usr_id = Global.usr_Id;
        usr_name = Global.usr_name;
        stat = OrderStat.New;
        wh_id = Global.wh_Id;
        this.ship = ship;
    }

    private Order(int id, String no, String date, int cus_id, String cus_name, int wh_id, OrderStat stat, int qty, float weight,
                  float amount, int usr_id, String usr_name, String pay, boolean ship, String remark) {
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
        this.usr_id = usr_id;
        this.usr_name = usr_name;
        this.pay = pay;
        this.ship = ship;
        this.remark = remark;
    }

    public static List<Order> retrieve(List<Order> orders) {
        orders.clear();
        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getorder(?,?)}", new String[]{String.valueOf(Global.wh_Id), String.valueOf(Global.usr_Id)});
            while (rs != null && rs.next()) {
                Order o = new Order(rs.getInt("id"), rs.getString("no"), rs.getString("order_date"),
                        rs.getInt("cus_id"), rs.getString("cus_name"), rs.getInt("wh_id"), OrderStat.valueOf(rs.getString("order_stat")),
                        rs.getInt("qty"), rs.getFloat("weight"), rs.getFloat("amount"), rs.getInt("usr_id"), rs.getString("usr_name"),
                        rs.getString("pay"), rs.getBoolean("ship"), rs.getString("remark"));
                orders.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        if (!Objects.equals(this.no, no)) {
            this.no = no;
            updateRecordStat();
        }
    }

    public String getDate() {
        return date;
    }

    public int getCus_id() {
        return cus_id;
    }

    public String getCus_name() {
        return cus_name;
    }

    public OrderStat getStat() {
        return stat;
    }

    public void setStat(OrderStat stat) {
        if (this.stat != stat) {
            this.stat = stat;
            updateRecordStat();
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id != id) {
            this.id = id;
            updateRecordStat();
        }
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
        if (this.itemCount != itemCount) {
            this.itemCount = itemCount;
            updateRecordStat();
        }

    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        if (this.qty != qty) {
            this.qty = qty;
            updateRecordStat();
        }
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        if (this.weight != weight) {
            this.weight = weight;
            updateRecordStat();
        }

    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        if (this.amount != amount) {
            this.amount = amount;
            updateRecordStat();
        }
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

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        if (!Objects.equals(this.pay, pay)) {
            this.pay = pay;
            updateRecordStat();
        }
    }

    public boolean isShip() {
        return ship;
    }

    public void setShip(boolean ship) {
        if (this.ship != ship) {
            this.ship = ship;
            updateRecordStat();
        }
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        if (!Objects.equals(this.remark, remark)) {
            this.remark = remark;
            updateRecordStat();
        }
    }

    public SqlResult save() {
        SqlResult result = new SqlResult();
        if (getRecordStat() != RecordStat.NULL) {
            try {
                String[] params = {String.valueOf(id), String.valueOf(cus_id), String.valueOf(stat), String.valueOf(wh_id), String.valueOf(usr_id),
                        String.valueOf(qty), String.valueOf(amount), String.valueOf(weight), String.valueOf(getRecordStat()), this.ship ? "1" : "0"};
                ResultSet rs = SqlServer.execute("{call POS.dbo.setorder(?,?,?,?,?,?,?,?,?,?)}", params);
                if (rs != null && rs.next()) {
                    result.setIden(rs.getInt("Iden"));
                    result.setMsg(rs.getString("Msg"));
                    if (result.getIden() > 0) {
                        setId(result.getIden());
                        if (getRecordStat() == RecordStat.I) {
                            setNo(rs.getString("order_no"));
                        }
                        setRecordStat(RecordStat.NULL);
                        for (OrderItem item : items) {
                            item.getOrder().setId(this.getId());
                            result = item.save();
                        }
                    } else result.setMsg("ไม่ได้รับคำตอบจาก server");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                result.setMsg("ไม่สามารถเชื่อมต่อกับ server");
            }
        } else result.setMsg("ไม่มีความเปลี่ยนแปลง");
        return result;
    }

    @Override
    public String getSearchString() {
        return cus_name;
    }
}
