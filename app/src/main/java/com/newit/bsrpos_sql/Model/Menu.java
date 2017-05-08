package com.newit.bsrpos_sql.Model;

import android.support.annotation.DrawableRes;

public class Menu {

    private int id;
    private String name;
    private Class activity;
    @DrawableRes
    private int icon;
    @DrawableRes
    private int bgColor;

    public Menu(int Id, String name, int icon, int bgColor, Class activity) {
        this.id = Id;
        this.name = name;
        this.icon = icon;
        this.bgColor = bgColor;
        this.activity = activity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public int getBgColor() {
        return bgColor;
    }

    public Class getActivity() {
        return activity;
    }
}
