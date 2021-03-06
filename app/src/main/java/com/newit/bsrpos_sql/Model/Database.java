package com.newit.bsrpos_sql.Model;

import java.io.Serializable;

public class Database implements Serializable {
    private String name;
    private String db;
    private int port_lan;
    private int port_wan;
    private String pwd;
    private String user;
    private String ip_lan;
    private String ip_wan;
    private String appversion;

    public Database() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public int getPort_lan() {
        return port_lan;
    }

    public void setPort_lan(int port_lan) {
        this.port_lan = port_lan;
    }

    public int getPort_wan() {
        return port_wan;
    }

    public void setPort_wan(int port_wan) {
        this.port_wan = port_wan;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIp_lan() {
        return ip_lan;
    }

    public void setIp_lan(String ip_lan) {
        this.ip_lan = ip_lan;
    }

    public String getIp_wan() {
        return ip_wan;
    }

    public void setIp_wan(String ip_wan) {
        this.ip_wan = ip_wan;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getPrefix() {
        return db + ".V" + appversion.replace('.', '_') + ".";
    }
}
