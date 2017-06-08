package com.newit.bsrpos_sql.Util;

import android.os.AsyncTask;

import com.newit.bsrpos_sql.Activity.ActBase;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlQuery extends AsyncTask<String, String, ResultSet> {

    private String spName;
    private String[] spParams;
    private ActBase activity;
    private Object caller;
    private int tag;

    public SqlQuery(ActBase activity, int tag, String spName, String[] spParams) {
        initialization(activity, tag, spName, spParams);
    }

    public SqlQuery(ActBase activity, int tag, String spName, String[] spParams, Object caller) {
        this.caller = caller;
        initialization(activity, tag, spName, spParams);
    }

    private void initialization(ActBase activity, int tag, String spName, String[] spParams) {
        this.tag = tag;
        this.spName = spName;
        this.spParams = spParams;
        this.activity = activity;
        execute();
    }

    @Override
    protected void onPreExecute() {
        if (caller == null) activity.showProgressDialog("กำลังดึงข้อมูลจาก ERP...");
    }

    @Override
    protected ResultSet doInBackground(String[] params) {
        try {
            Connection conn = SqlConnect.connect(activity);
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
            } else throw (new SQLException());
        } catch (SQLException e) {
            e.printStackTrace();

            //   activity.MessageBox(spName + " execution error : " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ResultSet rs) {
        if (caller == null) activity.hideProgressDialog();
        try {
            activity.queryReturn(rs, tag, caller);
        } catch (SQLException e) {
            e.printStackTrace();
            //     activity.MessageBox(spName + " post-execution error : " + e.getMessage());
        }
    }

    public static ResultSet executeWait(ActBase activity, String spName, String[] spParams) {
        try {
            Connection conn = SqlConnect.connect(activity);
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
            } else throw (new SQLException());
        } catch (SQLException e) {
            e.printStackTrace();
            //     activity.MessageBox(spName + " execution error : " + e.getMessage());
        }
        return null;
    }
}
