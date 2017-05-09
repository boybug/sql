package com.newit.bsrpos_sql.Model;


import com.newit.bsrpos_sql.Util.SqlServer;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Warehouse extends ModelBase implements Serializable {
    private static final long serialVersionUID = 6L;
    private int id;
    private String name;

    public Warehouse(int id, String name) {
        super(false);
        this.id = id;
        this.name = name;
    }

    public static List<Warehouse> retrieve(List<Warehouse> warehouses) {
        warehouses.clear();
        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getwh(" + Global.usr_Id + ")}");
            while (rs.next()) {
                Warehouse w = new Warehouse(rs.getInt("wh_Id"), rs.getString("wh_name"));
                warehouses.add(w);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warehouses;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

