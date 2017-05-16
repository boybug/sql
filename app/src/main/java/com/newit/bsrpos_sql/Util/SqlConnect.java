package com.newit.bsrpos_sql.Util;


import com.newit.bsrpos_sql.Model.Global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnect {

    private static Connection conn;

    public static Connection connect() {
        if (conn == null) {

            final String connStrInternet = "jdbc:jtds:sqlserver://" + Global.database.getIp_wan() + ":" + String.valueOf(Global.database.getPort()) + "/" + Global.database.getDb();
            final String connStrIntranet = "jdbc:jtds:sqlserver://" + Global.database.getIp_lan() + ":" + String.valueOf(Global.database.getPort()) + "/" + Global.database.getDb();
            DriverManager.setLoginTimeout(10);
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                SqlConnect.conn = DriverManager.getConnection(Global.isLocal ? connStrIntranet : connStrInternet, Global.database.getUser(), Global.database.getPwd());
                return conn;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return conn;
    }

    public static void disconnect() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }
}
