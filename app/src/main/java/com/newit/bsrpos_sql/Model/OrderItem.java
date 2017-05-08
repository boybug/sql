package com.newit.bsrpos_sql.Model;

import com.newit.bsrpos_sql.Util.SqlServer;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItem extends ModelBase implements Serializable {
    private Order order;
    private int id;
    private int no;
    private Product product;
    private int qty;
    private float price;
    private float weight;
    private float amount;
    private int uom_id;
    private static final long serialVersionUID = 3L;

    private List<StepPrice> prices;

    public OrderItem(Order order, int no, Product product) {
        super(true);
        qty = 0;
        price = 0;
        weight = 0;
        amount = 0;
        uom_id = product.getUom_id();
        initialization(order, no, product);
    }

    public OrderItem(Order order, int id, int no, Product product, int qty, float price, float weight, float amount, int uom_id) {
        super(false);
        this.id = id;
        this.qty = qty;
        this.price = price;
        this.weight = weight;
        this.amount = amount;
        this.uom_id = uom_id;
        initialization(order, no, product);
    }

    private void initialization(Order order, int no, Product product) {
        this.order = order;
        this.no = no;
        this.product = product;
        this.order.setItemCount(order.getItemCount() + 1);
        this.prices = new ArrayList<>();

        //price
        if (order.getStat() == OrderStat.New && product.isStepPrice()) {
            try {
                ResultSet rs = SqlServer.execute("{call POS.dbo.getstepprice(" + Integer.valueOf(product.getId()) + "," + Integer.valueOf(Global.wh_Id) + ")}");
                while (rs.next()) {
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
        qty += delta;
        weight += delta * product.getWeight();
        if (product.isStepPrice() && !prices.isEmpty()) {
            for (StepPrice p : prices) {
                if (qty >= p.getFrom() && qty <= p.getTo()) {
                    price = p.getPrice();
                    break;
                }
            }
        } else price = product.getPrice();
        amount = qty * price;

        order.setQty(order.getQty() + delta);
        order.setWeight(order.getWeight() + (delta * product.getWeight()));
        order.setAmount(order.getAmount() + (delta * price));
    }

    public int getUom_id() {
        return uom_id;
    }

}
