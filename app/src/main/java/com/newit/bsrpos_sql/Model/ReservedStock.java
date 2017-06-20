package com.newit.bsrpos_sql.Model;


import com.google.firebase.database.Exclude;

public class ReservedStock {
    private int wh_Id;
    private int prod_Id;
    private int qty;
    private String fbkey;

    public ReservedStock(int wh_Id, int prod_Id, int qty, String fbkey) {
        this.wh_Id = wh_Id;
        this.prod_Id = prod_Id;
        this.qty = qty;
        this.fbkey = fbkey;
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

    @Exclude
    public String getFbkey() {
        return fbkey;
    }

    public void setWh_Id(int wh_Id) {
        this.wh_Id = wh_Id;
    }

    public void setProd_Id(int prod_Id) {
        this.prod_Id = prod_Id;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Exclude
    public void setFbkey(String fbkey) {
        this.fbkey = fbkey;
    }
}
