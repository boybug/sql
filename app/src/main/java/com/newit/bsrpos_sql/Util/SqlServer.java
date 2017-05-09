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

        final String connStrInternet = "xxx";
        final String connStrIntranet = "xxx";
        final String user = "xxx";
        final String password = "xxx";

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