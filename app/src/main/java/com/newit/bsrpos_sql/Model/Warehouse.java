package com.newit.bsrpos_sql.Model;


public class Warehouse extends ModelBase {
    private static final long serialVersionUID = 6L;
    private final int id;
    private final String name;

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

    @Override
    public String getSearchString() {
        return name;
    }
}

