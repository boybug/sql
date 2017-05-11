package com.newit.bsrpos_sql.Model;


public class Customer extends ModelBase {

    private static final long serialVersionUID = 1L;
    private final int id;
    private final String name;
    private final String addr;
    private final String tel;
    private final boolean ship;

    public Customer(int id, String name, String addr, String tel, boolean ship) {
        super(false);
        this.id = id;
        this.name = name;
        this.addr = addr;
        this.tel = tel;
        this.ship = ship;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }

    public String getTel() {
        return tel;
    }

    public boolean isShip() {
        return ship;
    }

    @Override
    public String getSearchString() {
        return name;
    }
}
