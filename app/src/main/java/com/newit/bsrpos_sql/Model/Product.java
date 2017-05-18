package com.newit.bsrpos_sql.Model;

public class Product extends ModelBase {

    private static final long serialVersionUID = 4L;
    private final int id;
    private final String name;
    private int stock;
    private final float weight;
    private final String color;
    private final boolean stepPrice;
    private final float price;
    private final int uom_id;
    private final int wh_Id;
    private FbStock fbstock;

    public Product(int id, String name, int stock, float weight, String color, boolean stepPrice, float price, int uom_id, int wh_Id) {
        super(false);
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.weight = weight;
        this.color = color;
        this.stepPrice = stepPrice;
        this.price = price;
        this.uom_id = uom_id;
        this.wh_Id = wh_Id;
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

    public int getWh_Id() {
        return wh_Id;
    }

    @Override
    public String getSearchString() {
        return name;
    }

    public FbStock getFbstock() {
        return fbstock;
    }

    public void setFbstock(FbStock fbstock) {
        this.fbstock = fbstock;
    }

    public void addReserve(int delta) {
        if (fbstock != null)
            fbstock.setReserve(fbstock.getReserve() + delta);
    }

    public int getRemaining() {
        return stock - (fbstock == null ? 0 : fbstock.getReserve());
    }
}
