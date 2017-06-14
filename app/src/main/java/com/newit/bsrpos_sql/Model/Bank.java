package com.newit.bsrpos_sql.Model;


public class Bank extends ModelBase {

    private static final long serialVersionUID = 9L;
    private final int id;
    private final String name;

    public Bank(int id, String name) {
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
