package com.newit.bsrpos_sql.Model;

import java.io.Serializable;

public class StepPrice extends ModelBase implements Serializable {

    private static final long serialVersionUID = 5L;
    private final int from;
    private final int to;
    private final float price;

    public StepPrice(int from, int to, float price) {
        super(false);
        this.from = from;
        this.to = to;
        this.price = price;
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

    @Override
    public String getSearchString() {
        return null;
    }
}
