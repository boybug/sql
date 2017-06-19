package com.newit.bsrpos_sql.Model;

import com.newit.bsrpos_sql.Activity.ActBase;
import com.newit.bsrpos_sql.Util.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItem extends ModelBase {
    private static final long serialVersionUID = 3L;
    private Order order;
    private int id;
    private int no;
    private Product product;
    private int qty;
    private int qtybak;
    private float price;
    private float weight;
    private float amount;
    private int uom_id;
    private int wh_Id;
    private List<StepPrice> prices = new ArrayList<>();

    public OrderItem(Order order, int no, Product product) {
        super(true);
        qty = 0;
        qtybak = 0;
        price = 0;
        weight = 0;
        amount = 0;
        uom_id = product.getUom_id();
        this.wh_Id = product.getWh_Id();
        this.order = order;
        this.no = no;
        this.product = product;
    }

    public OrderItem(Order order, int id, int no, Product product, int qty, float price, float weight, float amount, int uom_id, int wh_Id) {
        super(false);
        this.id = id;
        this.qty = qty;
        this.qtybak = qty;
        this.price = price;
        this.weight = weight;
        this.amount = amount;
        this.uom_id = uom_id;
        this.wh_Id = wh_Id;
        this.order = order;
        this.no = no;
        this.product = product;
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

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        if (this.no != no) {
            this.no = no;
            updateRecordStat();
        }
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        if (this.product != product) {
            this.product = product;
            updateRecordStat();
        }
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        if (this.qty != qty) {
            this.qty = qty;
            setAmount(qty * price);
            updateRecordStat();
        }
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        if (this.price != price) {
            this.price = price;
            setAmount(qty * price);
            updateRecordStat();
        }
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        if (this.weight != weight) {
            updateRecordStat();
            this.weight = weight;
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

    public int getWh_Id() {
        return wh_Id;
    }

    public Order getOrder() {
        return order;
    }

    public List<StepPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<StepPrice> prices) {
        this.prices = prices;
        updatePrice();
    }

    private void updatePrice() {
        for (StepPrice p : prices) {
            if (qty >= p.getFrom() && qty <= p.getTo()) {
                setPrice(p.getPrice());
                break;
            }
        }
    }

    public void addQty(int delta) {
        setQty(qty + delta);
        setWeight(weight + (delta * product.getWeight()));
        if (product.isStepPrice() && !prices.isEmpty()) {
            updatePrice();
        } else setPrice(product.getPrice());
        order.updateHeader();
    }

    public int getUom_id() {
        return uom_id;
    }

    public SqlResult save(ActBase activity) {
        SqlResult result = new SqlResult();
        if (getRecordStat() != RecordStat.NULL) {
            try {
                String[] params = {String.valueOf(order.getId()), String.valueOf(id), String.valueOf(no), String.valueOf(product.getId()),
                        String.valueOf(qty), String.valueOf(price), String.valueOf(amount), String.valueOf(weight), String.valueOf(uom_id),
                        String.valueOf(getRecordStat()), String.valueOf(product.getWh_Id()), product.getFbstock().getKey()};
                ResultSet rs = SqlQuery.executeWait(activity, "{call " + Global.getDatabase(activity).getPrefix() + "setorderitem(?,?,?,?,?,?,?,?,?,?,?,?)}", params);
                if (rs != null && rs.next()) {
                    result.setIden(rs.getInt("Iden"));
                    result.setMsg(rs.getString("Msg"));
                    if (result.getIden() > 0) {
                        setId(result.getIden());
                        setRecordStat(RecordStat.NULL);
                        qtybak = 0;
                    } else result.setMsg("ไม่ได้รับคำตอบจาก server");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                result.setMsg("ไม่สามารถเชื่อมต่อกับ server");
            }
        }
        return result;
    }

    public SqlResult delete(ActBase activity) {
        SqlResult result = new SqlResult();
        if (getRecordStat() != RecordStat.NULL) {
            try {
                ResultSet rs = SqlQuery.executeWait(activity, "{call " + Global.getDatabase(activity).getPrefix() + "deleteorderitem(?)}", new String[]{String.valueOf(id)});
                if (rs != null && rs.next()) {
                    result.setIden(rs.getInt("Iden"));
                    result.setMsg(rs.getString("Msg"));
                    if (result.getIden() > 0) {
                        setId(result.getIden());
                        setRecordStat(RecordStat.NULL);
                    } else result.setMsg("ไม่ได้รับคำตอบจาก server");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                result.setMsg("ไม่สามารถเชื่อมต่อกับ server");
            }
        }
        return result;
    }

    @Override
    public String getSearchString() {
        return product.getName();
    }

    public int getDelta() {
        return qty - qtybak;
    }
}
