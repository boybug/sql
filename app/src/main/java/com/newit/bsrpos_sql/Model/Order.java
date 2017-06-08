package com.newit.bsrpos_sql.Model;

import com.newit.bsrpos_sql.Activity.ActBase;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order extends ModelBase {
    private static final long serialVersionUID = 2L;
    private int id;
    private String no;
    private final String date;
    private final int wh_grp_Id;
    private final int cus_id;
    private final String cus_name;
    private OrderStat stat;
    private List<OrderItem> items = new ArrayList<>();
    private List<OrderItem> deletingItems = new ArrayList<>();
    private int qty;
    private float weight;
    private float amount;
    private final int usr_id;
    private final String usr_name;
    private OrderPay pay;
    private boolean ship;
    private String remark;
    private float paid;
    private float charge;
    private float refund;

    public Order(int cus_id, String cus_name, boolean ship) {
        super(true);
        date = new Date().toString();
        qty = 0;
        weight = 0;
        amount = 0;
        this.cus_id = cus_id;
        this.cus_name = cus_name;
        usr_id = Global.user.getId();
        usr_name = Global.user.getName();
        stat = OrderStat.New;
        wh_grp_Id = Global.wh_Grp_Id;
        this.ship = ship;
        this.pay = OrderPay.Cash;
        this.paid = 0;
        this.charge = 0;
        this.refund = 0;
    }

    public Order(int id, String no, String date, int cus_id, String cus_name, int wh_grp_Id, OrderStat stat, int qty, float weight,
                 float amount, int usr_id, String usr_name, OrderPay pay, boolean ship, String remark, float paid, float charge, float refund) {
        super(false);
        this.id = id;
        this.no = no;
        this.date = date;
        this.cus_id = cus_id;
        this.cus_name = cus_name;
        this.wh_grp_Id = wh_grp_Id;
        this.stat = stat;
        this.qty = qty;
        this.weight = weight;
        this.amount = amount;
        this.usr_id = usr_id;
        this.usr_name = usr_name;
        this.pay = pay;
        this.ship = ship;
        this.remark = remark;
        this.paid = paid;
        this.charge = charge;
        this.refund = refund;
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

    public int getWh_grp_Id() {
        return wh_grp_Id;
    }

    public OrderPay getPay() {
        return pay;
    }

    public void setPay(OrderPay pay) {
        if (this.pay != pay) {
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

    public float getPaid() {
        return paid;
    }

    public void setPaid(float paid) {
        if (!Objects.equals(this.paid, paid)) {
            this.paid = paid;
            updateRecordStat();
        }
    }

    public float getCharge() {
        return charge;
    }

    public void setCharge(float charge) {
        if (!Objects.equals(this.charge, charge)) {
            this.charge = charge;
            updateRecordStat();
        }
    }

    public float getRefund() {
        return refund;
    }

    public void setRefund(float refund) {
        if (!Objects.equals(this.refund, refund)) {
            this.refund = refund;
            updateRecordStat();
        }
    }

    public List<OrderItem> getDeletingItems() {
        return deletingItems;
    }

    public void setDeletingItems(List<OrderItem> deletingItems) {
        this.deletingItems = deletingItems;
    }

    @Override
    public String getSearchString() {
        return cus_name+no;
    }

    public OrderItem findItem(int id) {
        for (OrderItem item : items) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }

    public SqlResult save(ActBase activity) {
        SqlResult result = new SqlResult();
        if (getRecordStat() != RecordStat.NULL) {
            try {
                String[] params = {String.valueOf(id), String.valueOf(cus_id), String.valueOf(stat), String.valueOf(wh_grp_Id), String.valueOf(usr_id),
                        String.valueOf(qty), String.valueOf(amount), String.valueOf(weight), String.valueOf(getRecordStat()), ship ? "1" : "0", };
                ResultSet rs = SqlQuery.executeWait(activity, "{call " + Global.database.getPrefix() + "setorder(?,?,?,?,?,?,?,?,?,?)}", params);
                if (rs != null && rs.next()) {
                    result.setIden(rs.getInt("Iden"));
                    result.setMsg(rs.getString("Msg"));
                    if (result.getIden() > 0) {
                        setId(result.getIden());
                        if (getRecordStat() == RecordStat.I) {
                            setId(result.getIden());
                            setNo(rs.getString("order_no"));
                        }
                        setRecordStat(RecordStat.NULL);

                        //loop to save orderitems
                        for (OrderItem item : deletingItems) {
                            item.getOrder().setId(this.getId());
                            item.setRecordStat(RecordStat.D);
                            result = item.delete(activity);
                        }
                        deletingItems.clear();
                        for (OrderItem item : items) {
                            item.getOrder().setId(this.getId());
                            result = item.save(activity);
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

    public void updateHeader() {
        int tempQty = 0;
        float tempWeight = 0, tempAmount = 0;
        for (OrderItem i : items) {
            tempQty += i.getQty();
            tempWeight += i.getWeight();
            tempAmount += i.getAmount();
        }
        setQty(tempQty);
        setWeight(tempWeight);
        setAmount(tempAmount);
    }
}
