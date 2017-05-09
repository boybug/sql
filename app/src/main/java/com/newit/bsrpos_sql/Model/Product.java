package com.newit.bsrpos_sql.Model;

import com.newit.bsrpos_sql.Util.SqlServer;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Product extends ModelBase implements Serializable {

    private static final long serialVersionUID = 4L;
    private int id;
    private String name;
    private int stock;
    private float weight;
    private String color;
    private boolean stepPrice;
    private float price;
    private int uom_id;

    public Product(int id, String name, int stock, float weight, String color, boolean stepPrice, float price, int uom_id) {
        super(false);
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.weight = weight;
        this.color = color;
        this.stepPrice = stepPrice;
        this.price = price;
        this.uom_id = uom_id;
    }

    public static List<Product> retrieve(List<Product> products) {
        products.clear();
        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getproduct(" + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                Product p = new Product(rs.getInt("prod_Id"), rs.getString("prod_name"), rs.getInt("stock"), rs.getFloat("weight"), rs.getString("color"), rs.getBoolean("stepprice"), rs.getFloat("price"), rs.getInt("uom_id"));
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static Product retrieve(int prod_Id, int wh_Id, int uom_Id) {
        Product p = null;
        try {
            ResultSet rs1 = SqlServer.execute("{call POS.dbo.getproductbyid(" + String.valueOf(prod_Id) + "," + String.valueOf(wh_Id) + "," + String.valueOf(uom_Id) + ")}");
            if (rs1.next()) {
                p = new Product(rs1.getInt("prod_Id"), rs1.getString("prod_name"), rs1.getInt("stock"), rs1.getFloat("weight"), rs1.getString("color"), rs1.getBoolean("stepprice"), rs1.getFloat("price"), rs1.getInt("uom_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getWeight() {
        return weight;
    }

    public String getColor() {
        return color;
    }

    public boolean isStepPrice() {
        return stepPrice;
    }

    public float getPrice() {
        return price;
    }

    public int getUom_id() {
        return uom_id;
    }
}
