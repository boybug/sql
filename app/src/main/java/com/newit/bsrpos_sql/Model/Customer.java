package com.newit.bsrpos_sql.Model;


import com.newit.bsrpos_sql.Util.SqlServer;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int id;
    private final String name;
    private final String addr;
    private final String tel;
    private final boolean ship;

    private Customer(int id, String name, String addr, String tel, boolean ship) {
        this.id = id;
        this.name = name;
        this.addr = addr;
        this.tel = tel;
        this.ship = ship;
    }

    public static List<Customer> retrieve(List<Customer> customers) {
        customers.clear();
        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getcus(?)}", new String[]{String.valueOf(Global.wh_Id)});
            while (rs != null && rs.next()) {
                Customer c = new Customer(rs.getInt("cus_Id"), rs.getString("cus_name"), rs.getString("cus_addr"), rs.getString("cus_tel"), rs.getBoolean("cus_ship"));
                customers.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
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
