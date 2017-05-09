package com.newit.bsrpos_sql.Model;


import com.newit.bsrpos_sql.Util.SqlServer;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public static List<Customer> retrieve(List<Customer> customers) {
        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getcus(" + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                Customer c = new Customer(rs.getInt("cus_Id"), rs.getString("cus_name"), rs.getString("cus_addr"), rs.getString("cus_tel"), rs.getBoolean("cus_ship"));
                customers.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
}
