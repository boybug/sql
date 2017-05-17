package com.newit.bsrpos_sql.Model;

public class FbStock {
    private int prod_id;
    private int reserve;

    public FbStock() {
    }

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public int getReserve() {
        return reserve;
    }

    public void setReserve(int reserve) {
        this.reserve = reserve;
    }
}
