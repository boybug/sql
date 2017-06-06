package com.newit.bsrpos_sql.Util;


import com.newit.bsrpos_sql.Activity.ActBase;
import com.newit.bsrpos_sql.Model.Global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnect {

    private static Connection conn;

    public static Connection connect(ActBase activity) throws SQLException {
        if (conn == null || !conn.isValid(5)) {
            final String connStrInternet = "jdbc:jtds:sqlserver://" + Global.database.getIp_wan() + ":" + String.valueOf(Global.database.getPort()) + "/" + Global.database.getDb();
            final String connStrIntranet = "jdbc:jtds:sqlserver://" + Global.database.getIp_lan() + ":" + String.valueOf(Global.database.getPort()) + "/" + Global.database.getDb();
            DriverManager.setLoginTimeout(10);
            try {
                activity.showProgressDialog("กำลังเชื่อมต่อกับเซิร์ฟเวอร์ ERP...");
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                SqlConnect.conn = DriverManager.getConnection(Global.isLocal ? connStrIntranet : connStrInternet, Global.database.getUser(), Global.database.getPwd());
                activity.hideProgressDialog();
                return conn;
            } catch (Exception e) {
                e.printStackTrace();
                activity.connectError(e);
            }
            return null;
        }
        return conn;
    }

    public static void disconnect(ActBase activity) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    activity.connectError(e);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            conn = null;
        }
    }
}
