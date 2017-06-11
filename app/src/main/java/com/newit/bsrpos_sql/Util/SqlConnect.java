package com.newit.bsrpos_sql.Util;


import com.newit.bsrpos_sql.Activity.ActBase;
import com.newit.bsrpos_sql.Model.Global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnect {

    private static Connection conn;

    public static Connection connect(final ActBase activity) throws SQLException {
        if (conn == null || conn.isClosed()) {
            final String connStrInternet = "jdbc:jtds:sqlserver://" + Global.database.getIp_wan() + ":" + String.valueOf(Global.database.getPort_wan()) + "/" + Global.database.getDb();
            final String connStrIntranet = "jdbc:jtds:sqlserver://" + Global.database.getIp_lan() + ":" + String.valueOf(Global.database.getPort_lan()) + "/" + Global.database.getDb();
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                SqlConnect.conn = DriverManager.getConnection(Global.isLocal ? connStrIntranet : connStrInternet, Global.database.getUser(), Global.database.getPwd());
                activity.hideProgressDialog();
                return conn;
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.hideProgressDialog();
                        activity.MessageBox("ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ ERP");
                    }
                });
            }
            return null;
        }
        return conn;
    }

    public static void disconnect(final ActBase activity) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.hideProgressDialog();
                        activity.MessageBox("ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ ERP");
                    }
                });
                conn = null;
            }
        }
    }
}
