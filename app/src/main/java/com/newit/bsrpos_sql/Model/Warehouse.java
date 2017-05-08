package com.newit.bsrpos_sql.Model;


import java.io.Serializable;

public class Warehouse extends ModelBase implements Serializable {
    private int id;
    private String name;
    private static final long serialVersionUID = 6L;

    public Warehouse(int id, String name) {
        super(false);
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
