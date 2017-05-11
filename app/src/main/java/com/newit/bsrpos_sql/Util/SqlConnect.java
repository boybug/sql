package com.newit.bsrpos_sql.Util;


import com.newit.bsrpos_sql.Model.Global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnect {

    private static Connection conn;

    public static Connection connect() {
        if (conn == null) {
            final String connStrInternet = "xx";
            final String connStrIntranet = "xx";
            final String user = "xx";
            final String password = "xx";

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
