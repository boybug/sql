//package com.newit.bsrpos_sql.Util;
//
//import android.os.StrictMode;
//
//import com.newit.bsrpos_sql.Model.Global;
//
//import java.sql.CallableStatement;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class SqlServer {
//
//    private static Connection conn;
//
//    private static void connect() {
//
//        final String connStrInternet = "jdbc:jtds:sqlserver://203.114.108.46:11433/pos";
//        final String connStrIntranet = "jdbc:jtds:sqlserver://192.168.10.13:11433/pos";
//        final String user = "TM";
//
//        final String password = "@TM2013!!!";
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        DriverManager.setLoginTimeout(10);
//
//        try {
//            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//            SqlServer.conn = DriverManager.getConnection(Global.isLocal ? connStrIntranet : connStrInternet, user, password);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static ResultSet execute(String procedure, String[] params) {
//        try {
//            if (SqlServer.conn == null) SqlServer.connect();
//            if (conn != null) {
//                CallableStatement stmt = conn.prepareCall(procedure, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//                if (params != null) {
//                    for (int i = 0; i < params.length; i++) {
//                        stmt.setString(i + 1, params[i]);
//                    }
//                }
//                stmt.execute();
//                return stmt.getResultSet();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static void disconnect() {
//        if (SqlServer.conn != null) {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            conn = null;
//        }
//    }
//}