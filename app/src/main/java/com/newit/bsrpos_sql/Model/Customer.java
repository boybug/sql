package com.newit.bsrpos_sql.Model;


import java.io.Serializable;

public class Customer implements Serializable {

    private int id;
    private String name;
    private String addr;
    private String tel;
    private boolean ship;
    private static final long serialVersionUID = 1L;

    public Customer(int id, String name, String addr, String tel, boolean ship) {
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
}
