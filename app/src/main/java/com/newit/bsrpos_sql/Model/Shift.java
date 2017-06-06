package com.newit.bsrpos_sql.Model;


public class Shift extends ModelBase {
    private int id;
    private String name;
    private String user;
    private int wh_grp_Id;
    private String date;
    private boolean isClosed;
    private String tel;
    private String imei;

    public Shift() {
        super(true);
    }

    public Shift(int id, String name, String user, int wh_grp_Id, String date, boolean isClosed, String tel, String imei) {
        super(false);
        this.id = id;
        this.name = name;
        this.user = user;
        this.wh_grp_Id = wh_grp_Id;
        this.date = date;
        this.isClosed = isClosed;
        this.tel = tel;
        this.imei = imei;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public int getWh_grp_Id() {
        return wh_grp_Id;
    }

    public String getDate() {
        return date;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String getTel() {
        return tel;
    }

    public String getImei() {
        return imei;
    }

    @Override
    public String getSearchString() {
        return name;
    }
}
