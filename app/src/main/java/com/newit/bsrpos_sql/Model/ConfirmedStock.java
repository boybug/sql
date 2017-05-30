package com.newit.bsrpos_sql.Model;


public class ConfirmedStock {
    private int item_id;
    private int wh_Id;
    private int prod_Id;
    private int qty;

    public ConfirmedStock(int item_id, int wh_Id, int prod_Id, int qty) {
        this.item_id = item_id;
        this.wh_Id = wh_Id;
        this.prod_Id = prod_Id;
        this.qty = qty;
    }

    public int getItem_id() {
        return item_id;
    }

    public int getWh_Id() {
        return wh_Id;
    }

    public int getProd_Id() {
        return prod_Id;
    }

    public int getQty() {
        return qty;
    }
}
