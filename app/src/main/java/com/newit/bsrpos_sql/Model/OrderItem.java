package com.newit.bsrpos_sql.Model;

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
    private float price;
    private float weight;
    private float amount;
    private final int uom_id;
    private int wh_Id;
    private List<StepPrice> prices = new ArrayList<>();

    public OrderItem(Order order, int no, Product product) {
        super(true);
        qty = 0;
        price = 0;
        weight = 0;
        amount = 0;
        uom_id = product.getUom_id();
        initialization(order, no, product);
    }

    public OrderItem(Order order, int id, int no, Product product, int qty, float price, float weight, float amount, int uom_id, int wh_Id) {
        super(false);
        this.id = id;
        this.qty = qty;
        this.price = price;
        this.weight = weight;
        this.amount = amount;
        this.uom_id = uom_id;
        this.wh_Id = wh_Id;
        initialization(order, no, product);
    }

    private void initialization(Order order, int no, Product product) {
        this.order = order;
        this.no = no;
        this.product = product;
        this.prices = new ArrayList<>();

        //price
        if (order.getStat() == OrderStat.New && product.isStepPrice()) {
            try {
                ResultSet rs = SqlQuery.executeWait("{call " + Global.database.getPrefix() + "getstepprice(?,?)}", new String[]{String.valueOf(product.getId()), String.valueOf(product.getWh_Id())});
                while (rs != null && rs.next()) {
                    StepPrice p = new StepPrice(rs.getInt("from"), rs.getInt("to"), rs.getFloat("price"));
                    prices.add(p);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            updateRecordStat();
        }
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        if (this.price != price) {
            this.price = price;
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
    }

    public void addQty(int delta) {
        setQty(qty + delta);
        setWeight(weight + (delta * product.getWeight()));
        if (product.isStepPrice() && !prices.isEmpty()) {
            for (StepPrice p : prices) {
                if (qty >= p.getFrom() && qty <= p.getTo()) {
                    setPrice(p.getPrice());
                    break;
                }
            }
        } else setPrice(product.getPrice());
        setAmount(qty * price);

        order.updateHeader();
    }

    public int getUom_id() {
        return uom_id;
    }

    public SqlResult save() {
        SqlResult result = new SqlResult();
        if (getRecordStat() != RecordStat.NULL) {
            try {
                String[] params = {String.valueOf(order.getId()), String.valueOf(id), String.valueOf(no), String.valueOf(product.getId()),
                        String.valueOf(qty), String.valueOf(price), String.valueOf(amount), String.valueOf(weight), String.valueOf(uom_id),
                        String.valueOf(getRecordStat()), String.valueOf(product.getWh_Id())};
                ResultSet rs = SqlQuery.executeWait("{call " + Global.database.getPrefix() + "setorderitem(?,?,?,?,?,?,?,?,?,?,?)}", params);
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

    public SqlResult delete() {
        SqlResult result = new SqlResult();
        if (getRecordStat() != RecordStat.NULL) {
            try {
                ResultSet rs = SqlQuery.executeWait("{call " + Global.database.getPrefix() + "deleteorderitem(?)}", new String[]{String.valueOf(id)});
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
}
