package com.newit.bsrpos_sql.Model;

import java.io.Serializable;

public class Product extends ModelBase implements Serializable {

    private int id;
    private String name;
    private int stock;
    private float weight;
    private String color;
    private boolean stepPrice;
    private float price;
    private int uom_id;
    private static final long serialVersionUID = 4L;

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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
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

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getUom_id() {
        return uom_id;
    }
}
