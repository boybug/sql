package com.newit.bsrpos_sql.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class FbStock implements Serializable {
    private int prod_id;
    private int reserve;
    private String key;

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

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
