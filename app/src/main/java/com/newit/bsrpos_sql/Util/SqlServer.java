package com.newit.bsrpos_sql.Util;

import android.os.StrictMode;

import com.newit.bsrpos_sql.Model.Global;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlServer {

    public static Connection conn;

    public static void connect() {

        final String connStrInternet = "jdbc:jtds:sqlserver://203.114.108.46:11433/pos";
        final String connStrIntranet = "jdbc:jtds:sqlserver://192.168.10.13:11433/pos";
        final String user = "TM";
        final String password = "@TM2013!!!";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            SqlServer.conn = DriverManager.getConnection(Global.isLocal ? connStrIntranet : connStrInternet, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet execute(String procedure) {
        try {
            if (SqlServer.conn == null) SqlServer.connect();
            if (conn != null) {
                CallableStatement stmt = conn.prepareCall(procedure, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                stmt.execute();
                return stmt.getResultSet();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}