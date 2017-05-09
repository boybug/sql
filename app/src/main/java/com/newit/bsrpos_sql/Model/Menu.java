package com.newit.bsrpos_sql.Model;

public class Menu {

    private final int id;
    private final String name;
    private final Class activity;
    private final int icon;
    private final int bgColor;

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
