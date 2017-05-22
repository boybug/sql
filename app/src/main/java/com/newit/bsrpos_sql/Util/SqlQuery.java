package com.newit.bsrpos_sql.Util;

import android.os.AsyncTask;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlQuery extends AsyncTask<String, String, ResultSet> {

    private String spName;
    private String[] spParams;
    public sqlCallback delegate = null;
    private int tag;

    public SqlQuery(sqlCallback delegate, int tag, String spName, String[] spParams) {
        this.delegate = delegate;
        this.tag = tag;
        this.spName = spName;
        this.spParams = spParams;
        execute();
    }

    @Override
    protected ResultSet doInBackground(String[] params) {
        try {
            Connection conn = SqlConnect.connect();
            if (conn != null) {
                CallableStatement stmt = conn.prepareCall(spName, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                if (spParams != null) {
                    for (int i = 0; i < spParams.length; i++) {
                        stmt.setString(i + 1, spParams[i]);
                    }
                }
                stmt.setQueryTimeout(120);
                stmt.execute();
                return stmt.getResultSet();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ResultSet rs) {
        try {
            delegate.processFinish(rs, tag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet executeWait(String spName, String[] spParams) {

        try {
            Connection conn = SqlConnect.connect();
            if (conn != null) {
                CallableStatement stmt = conn.prepareCall(spName, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                if (spParams != null) {
                    for (int i = 0; i < spParams.length; i++) {
                        stmt.setString(i + 1, spParams[i]);
                    }
                }
                stmt.execute();
                return stmt.getResultSet();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
