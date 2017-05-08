package com.newit.bsrpos_sql.Util;

import android.os.StrictMode;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlServer {

    public static Connection conn;

    public static void connect() {

        final String connStr = "jdbc:jtds:sqlserver://203.114.108.46:11433/mas";
        final String user = "TM";
        final String password = "@TM2013!!!";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            SqlServer.conn = DriverManager.getConnection(connStr, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet query(String query) {

        try {
            if (SqlServer.conn == null) SqlServer.connect();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                return stmt.executeQuery(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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