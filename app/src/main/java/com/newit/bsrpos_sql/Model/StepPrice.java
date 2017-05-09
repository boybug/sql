package com.newit.bsrpos_sql.Model;

import com.newit.bsrpos_sql.Util.SqlServer;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StepPrice extends ModelBase implements Serializable {

    private static final long serialVersionUID = 5L;
    private int from;
    private int to;
    private float price;

    public StepPrice(int from, int to, float price) {
        super(false);
        this.from = from;
        this.to = to;
        this.price = price;
    }

    public static List<StepPrice> retrieve(List<StepPrice> stepPrices, int prod_Id) {
        stepPrices.clear();
        try {
            ResultSet rs = SqlServer.execute("{call POS.dbo.getstepprice(" + String.valueOf(prod_Id) + ", " + Integer.valueOf(Global.wh_Id) + ")}");
            while (rs.next()) {
                StepPrice sp = new StepPrice(rs.getInt("from"), rs.getInt("to"), rs.getFloat("price"));
                stepPrices.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepPrices;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public float getPrice() {
        return price;
    }
}
