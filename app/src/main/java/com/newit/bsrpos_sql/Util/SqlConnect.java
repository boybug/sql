package com.newit.bsrpos_sql.Util;


import com.newit.bsrpos_sql.Model.Global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnect {

    private static Connection conn;

    public static Connection connect() {
        if (conn == null) {
            final String connStrInternet = "jdbc:jtds:sqlserver://203.114.108.46:11433/pos";
            final String connStrIntranet = "jdbc:jtds:sqlserver://192.168.10.13:11433/pos";
            final String user = "TM";
            final String password = "@TM2013!!!";

            DriverManager.setLoginTimeout(10);
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                SqlConnect.conn = DriverManager.getConnection(Global.isLocal ? connStrIntranet : connStrInternet, user, password);
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
