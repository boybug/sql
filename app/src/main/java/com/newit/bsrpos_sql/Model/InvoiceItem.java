package com.newit.bsrpos_sql.Model;


public class InvoiceItem extends ModelBase {

    private static final long serialVersionUID = 8L;
    private int no;
    private String prod_name;
    private int qty;
    private float price;
    private float weight;
    private float amount;

    public InvoiceItem(int no, String prod_name, int qty, float price, float weight, float amount) {
        super(false);
        this.no = no;
        this.prod_name = prod_name;
        this.qty = qty;
        this.price = price;
        this.weight = weight;
        this.amount = amount;
    }

    public int getNo() {
        return no;
    }

    public String getProd_name() {
        return prod_name;
    }

    public int getQty() {
        return qty;
    }

    public float getPrice() {
        return price;
    }

    public float getAmount() {
        return amount;
    }

    @Override
    public String getSearchString() {
        return prod_name;
    }
}
